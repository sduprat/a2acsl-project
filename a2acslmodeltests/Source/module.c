#include <module.h>

int doActions(N_Action action, int it){
	switch (action){
	int res;
	int* y;
	int* z;
	case A1 :
		res = action1();
		state.id = state.id + 1;
		return res;
	case A2 :
		*z = 0;
		action2(it, y, z);
		if (*y==0) {
			states[*z].id = 0;
		} else {
			states[*y].id = *z;
		}
		return states[0].id;
	case A3 :
		if (status==0){
			return action3(action3(action3(0)));
		} else {
			init();
			return action1();
		}
	default :
		return -1;
	}
}

