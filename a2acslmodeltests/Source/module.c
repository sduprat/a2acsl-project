#include <module.h>
#include <actions.h>

int doActions(N_Action action, int it){
	switch (action){
	case A1 :
		int res = action1();
		state.id = state.id + 1;
		return res;
	case A2 :
		int* y;
		int* z;
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
	}
}

