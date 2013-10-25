typedef enum {
	A1 = 0,
	A2 = 1,
	A3 = 2
} N_Action;

typedef struct _State{
	int id;
} State;

State states[10];
State state;

int doActions(N_Action action, int it);
void init();
