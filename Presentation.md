## Introduction ##
A2ACSL is an Eclipse plug-in that allows automatic generation of ACSL annotations from a UML Activity diagram for data flow verification in a C program.

## UML Activity Diagrams ##
Activity Diagrams offer a clear and graphical representation of the flow of information through a sequence of actions and allow a detailed behavioral description of an operation. In our context, they are used to describe precisely the data flow and sequencing of actions in a C program.

## Formal Proof ##
The plug-in generates annotations in ANSI/ISO C Specification Language. The annotated code is then provided to Frama-C, an open source platform that verifies through formal proof if the provided code complies with the ACSL specification

## Links ##
OMG UML : http://www.uml.org/

ACSL : http://frama-c.com/acsl.html

Frama-C : http://frama-c.com/