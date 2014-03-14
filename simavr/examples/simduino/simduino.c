/*
	simduino.c

	Copyright 2008, 2009 Michel Pollet <buserror@gmail.com>
	Copyright 2013 Emanuele 'lemrey' Di Santo <lemrey@gmail.com>
	
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>

// networking
#include <sys/socket.h>
#include <arpa/inet.h>

// simavr
#include "sim_avr.h"
#include "sim_elf.h"
#include "avr_adc.h"
#include "avr_timer.h"
#include "avr_ioport.h"
#include "uart_pty.h"

#define LOOPBACK "127.0.0.1"

// main structure
avr_t * avr = NULL;

// IRQs for PORTB and ADC module
avr_irq_t *_irq;
avr_irq_t *_adc;

struct sockaddr_in sock_addr;
int sendto_sock, slen=sizeof(sock_addr);

uint32_t pin_state = 0;

/* Callback function for PWM pins */
void pwm_changed_hook(struct avr_irq_t * irq, uint32_t value, void * param)
{
	int pin;
	char buf[8];

	if ( strcmp(param, "2B") == 0 ) pin = 3;
	if ( strcmp(param, "0A") == 0 ) pin= 6;
	if ( strcmp(param, "0B") == 0 ) pin = 5;

	printf("pwm changed (%d %s) %d, %d\n", pin, (char*)param, pin, value);

	// format our buffer and send it over
	sprintf(buf, "%d,%d", pin, value);
	if ( sendto(sendto_sock, buf, strlen(buf), 0, (struct sockaddr *)&sock_addr, slen) == -1)
		fprintf(stderr, "sendto error\n");
}

/* Callback function for generic IO pins */
void pin_changed_hook(struct avr_irq_t * irq, uint32_t value, void * param)
{
	char buf[8];
	
	// only forward if actually changed from internal state
	if ( (pin_state & (1 << irq->irq)) != value )
	{
		printf("pin %d changed to %d\n", irq->irq, value);

		sprintf(buf, "%d,%d", irq->irq, value);
		if (sendto(sendto_sock, buf, strlen(buf), 0, (struct sockaddr *)&sock_addr, slen)==-1)
			fprintf(stderr, "sendto error\n");
	}

	// update pin state
	pin_state = (pin_state & ~(1 << irq->irq)) | (value << irq->irq);
}

/*
 * Networking thread, listens for pin changes over UDP socket,
 * and raises appropriate IRQs
 */
static void *recv_thread(void * param)
{
	int port = 0;
	char *tok;
	char type;
	char buf[32];

	struct sockaddr_in sock_addr;
	int recv_sock, slen=sizeof(sock_addr);
	
	if ((recv_sock=socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) == -1)
	{
		fprintf(stderr, "socket error\n");
		exit(1);
	}

	 memset((char *) &sock_addr, 0, sizeof(sock_addr));
	 sock_addr.sin_family = AF_INET;
	 sock_addr.sin_port = htons(5901);
	 sock_addr.sin_addr.s_addr = htonl(INADDR_ANY);
	 
	 if (bind(recv_sock, (struct sockaddr *)&sock_addr, sizeof(sock_addr)) == -1)
	 {
		fprintf(stderr, "bind failed\n");
		exit(1);
	 }
	 
	 while (1)
	 {
		 // clear our buffer
		 memset(buf, 0, 32);
		 
		 if (recvfrom(recv_sock, buf, 32, 0, NULL, (socklen_t * restrict)&slen) == -1)
		 {
		        fprintf(stderr, "recvfrom error\n");
			exit(1);
		 }

		printf("[udp]received : %s\n", buf);

		// tokenize our packed and raise IRQs accordingly
		type = buf[0];
		if (type == 'A')
		{
			// an analog pin changed
			tok  = strtok(buf+1, ",");
			port = atoi(tok);
			tok = strtok(NULL,",");

			printf("RAISE IRQ (analog) %d: %d\n", port, atoi(tok));
			avr_raise_irq(_adc+port, atoi(tok));
		}
		else
		{
			// a digital pin changed
			tok  = strtok(buf, ",");
			port = atoi(tok);
			tok = strtok(NULL,",");

			printf("RAISE IRQ %d: %d\n", port, atoi(tok));
			avr_raise_irq(_irq+port-8, atoi(tok));
		}
	}
	return NULL;
}

int init_socket()
{
	int PORT = 5900;

	if ((sendto_sock=socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP)) == -1)
	{
	    fprintf(stderr, "socket error\n");
	    exit(1);
	}

	memset((char *) &sock_addr, 0, sizeof(sock_addr));
	sock_addr.sin_family = AF_INET;
	sock_addr.sin_port = htons(PORT);

	// convert IP address
	if (inet_aton(LOOPBACK, &sock_addr.sin_addr) == 0)
	{
		fprintf(stderr, "inet_aton() failed\n");
		exit(1);
	}

	return 0;
}

int main(int argc, char *argv[])
{
	int state;
	const char *fname = argv[1];

	// our pseudo-terminal, used for serial comm.
	uart_pty_t uart_pty;

	// the elf program
	elf_firmware_t f;
	elf_read_firmware(fname, &f);

	avr = avr_make_mcu_by_name("atmega328p");
	if (!avr)
	{
	    fprintf(stderr, "failed initializing mcu\n");
	    exit(1);
	}

	// init avr structure and load the program
	avr_init(avr);
	avr_load_firmware(avr, &f);

	// set some parameters (could be read from cmdline)
	avr->frequency=16000000L;
	// necessary for ADC conversions
	avr->avcc = 5000;
	avr->aref=1100;

	// init the pseudo-terminal
	uart_pty_init(avr, &uart_pty);
	uart_pty_connect(&uart_pty, '0');

	// allocate IRQs for PORTB and ADC
	_irq = avr_alloc_irq(&avr->irq_pool, 0, 6, NULL);
	_adc = avr_alloc_irq(&avr->irq_pool, 0, 6, NULL);

	// monitor PWM pins
	avr_irq_t *pwm0A = avr_io_getirq(avr, AVR_IOCTL_TIMER_GETIRQ('0'), TIMER_IRQ_OUT_PWM0);
	avr_irq_t *pwm0B = avr_io_getirq(avr, AVR_IOCTL_TIMER_GETIRQ('0'), TIMER_IRQ_OUT_PWM1);
	avr_irq_t *pwm2B = avr_io_getirq(avr, AVR_IOCTL_TIMER_GETIRQ('2'), TIMER_IRQ_OUT_PWM1);
	avr_irq_register_notify(pwm0A, pwm_changed_hook, "0A");
	avr_irq_register_notify(pwm0B, pwm_changed_hook, "0B");
	avr_irq_register_notify(pwm2B, pwm_changed_hook, "2B");

	for (int i = 0; i < 6; i++ )
	{
		// connect all pins on PORTB (to be used an digital inputs)
		avr_connect_irq(_irq+i, avr_io_getirq( avr, AVR_IOCTL_IOPORT_GETIRQ('B'), i) );
		/* connect all pins on PORTC (to be used as analog inputs)
		 * note that we're actually connecting to the ADC module */
		avr_connect_irq(_adc+i, avr_io_getirq( avr, AVR_IOCTL_ADC_GETIRQ, i ) );
	}

	/* monitor PORTD pins, D0 and D1 are reserved for serial communication */
	avr_irq_register_notify(avr_io_getirq(avr, AVR_IOCTL_IOPORT_GETIRQ('D'), 2), pin_changed_hook, "hook");
	avr_irq_register_notify(avr_io_getirq(avr, AVR_IOCTL_IOPORT_GETIRQ('D'), 4), pin_changed_hook, "hook");
	avr_irq_register_notify(avr_io_getirq(avr, AVR_IOCTL_IOPORT_GETIRQ('D'), 7), pin_changed_hook, "hook");

	// initialize sendto socket (outbound)
	init_socket();

	// initialize pin monitor thread
	pthread_t run;
	pthread_create(&run, NULL, recv_thread, NULL);

	// enter main cycle
	state = cpu_Running;
	while ((state != cpu_Done) && (state != cpu_Crashed))
	{
		state = avr_run(avr);
	}

	uart_pty_stop(&uart_pty);
	printf("bye\n");
}
