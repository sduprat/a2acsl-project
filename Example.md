An example can be found at:

https://svn.codespot.com/a/eclipselabs.org/a2acsl-project/trunk/a2acslmodeltests/a2acslmodeltests.zip

To use this model, it has to be imported in Eclipse using Import.

# Source code #
## module.h ##
```

#include <actions.h>

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
void init();
int doActions(N_Action action, int it);
```

## action.h ##
```

int status;

int action1();
void action2(int x, /*out*/ int* y, /*inout*/ int* z);
int action3(int x);
```

## module.c ##
```

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
```

# Class diagram #
The following diagram describes the C structure of the modules.

<img src='https://svn.codespot.com/a/eclipselabs.org/a2acsl-project/wiki/images/classes.png' />

# Activity Diagram for doActions #
<img src='https://svn.codespot.com/a/eclipselabs.org/a2acsl-project/wiki/images/act.png' />

# Generation #
Once the model designed, right clicking on the .uml file and selecting Activity2Acsl generates the following files :

  * doActions\_stubs.h : Contains stub annotations to be placed before calle doperations declarations
  * doActions.h : Contains doActions contract, to be placed before doActions declarations

After annotating the header files, we get the following files:

## actions.h ##
```

int status;

/* ========== Annotations for called operation action1 ========== */
/* ========== Definition of observers structure ========== */
typedef struct _action1_context {
int result;
}action1_context;

/* ========== Declaration of context observers ========== */
/*@ ghost int size_doActions_action1_context;*/
/*@ ghost action1_context doActions_action1_context[1];*/

/* ========== Stub annotations ========== */
/*@
assigns size_doActions_action1_context;
ensures doActions_action1_context[\old(size_doActions_action1_context)].result == \result;
ensures size_doActions_action1_context == \old(size_doActions_action1_context) + 1;
*/
int action1();

/* ========== Annotations for called operation action2 ========== */
/* ========== Definition of observers structure ========== */
typedef struct _action2_context {
struct{
int x;
int z;
} _in;
struct{
int y;
int z;
} _out;
}action2_context;

/* ========== Declaration of context observers ========== */
/*@ ghost int size_doActions_action2_context;*/
/*@ ghost action2_context doActions_action2_context[1];*/

/* ========== Stub annotations ========== */
/*@
assigns *y, *z, size_doActions_action2_context, doActions_action2_context[size_doActions_action2_context]._in;
ensures doActions_action2_context[\old(size_doActions_action2_context)]._in.x == x;
ensures doActions_action2_context[\old(size_doActions_action2_context)]._in.z == \old(*z);
ensures doActions_action2_context[\old(size_doActions_action2_context)]._out.y == *y;
ensures doActions_action2_context[\old(size_doActions_action2_context)]._out.z == *z;
ensures size_doActions_action2_context == \old(size_doActions_action2_context) + 1;
*/
void action2(int x, /*out*/ int* y, /*inout*/ int* z);

/* ========== Annotations for called operation action3 ========== */
/* ========== Definition of observers structure ========== */
typedef struct _action3_context {
struct{
int x;
} _in;
int result;
}action3_context;

/* ========== Declaration of context observers ========== */
/*@ ghost int size_doActions_action3_context;*/
/*@ ghost action3_context doActions_action3_context[3];*/

/* ========== Stub annotations ========== */
/*@
assigns size_doActions_action3_context, doActions_action3_context[size_doActions_action3_context]._in;
ensures doActions_action3_context[\old(size_doActions_action3_context)]._in.x == x;
ensures doActions_action3_context[\old(size_doActions_action3_context)].result == \result;
ensures size_doActions_action3_context == \old(size_doActions_action3_context) + 1;
*/
int action3(int x);
```

## module.h ##
```

#include <actions.h>

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

/* ========== Annotations for called operation init ========== */
/* ========== Definition of observers structure ========== */
typedef struct _init_context {
}init_context;

/* ========== Declaration of context observers ========== */
/*@ ghost int size_doActions_init_context;*/
/*@ ghost init_context doActions_init_context[1];*/

/* ========== Stub annotations ========== */
/*@
assigns size_doActions_init_context;
ensures size_doActions_init_context == \old(size_doActions_init_context) + 1;
*/
void init();

/* ========== Function Contract ========== */
/*@
behavior B0:
assumes action == A1;
requires size_doActions_action1_context == 0;
ensures \result == doActions_action1_context[0].result;
ensures \let state_1_B0={ \old(state) \with .id = (int)((\old(state)).id + 1)};state == state_1_B0;
behavior B1:
assumes action == A2;
assumes \let y_1_B1=doActions_action2_context[0]._out.y;y_1_B1 == 0;
requires size_doActions_action2_context == 0;
ensures doActions_action2_context[0]._in.z == 0;
ensures doActions_action2_context[0]._in.x == it;
ensures \let z_1_B1=doActions_action2_context[0]._out.z;\let states_1_B1={\old(states) \with [z_1_B1] = (State) ({ \old(states[z_1_B1]) \with .id = (int)(0)} ) };\result == (states_1_B1[0]).id;
ensures \let z_1_B1=doActions_action2_context[0]._out.z;\let states_1_B1={\old(states) \with [z_1_B1] = (State) ({ \old(states[z_1_B1]) \with .id = (int)(0)} ) };states == states_1_B1;
behavior B2:
assumes action == A2;
assumes \let y_1_B2=doActions_action2_context[0]._out.y;y_1_B2 != 0;
requires size_doActions_action2_context == 0;
ensures doActions_action2_context[0]._in.z == 0;
ensures doActions_action2_context[0]._in.x == it;
ensures \let y_1_B2=doActions_action2_context[0]._out.y;\let z_1_B2=doActions_action2_context[0]._out.z;\let states_1_B2={\old(states) \with [y_1_B2] = (State) ({ \old(states[y_1_B2]) \with .id = (int)(z_1_B2)} ) };\result == (states_1_B2[0]).id;
ensures \let y_1_B2=doActions_action2_context[0]._out.y;\let z_1_B2=doActions_action2_context[0]._out.z;\let states_1_B2={\old(states) \with [y_1_B2] = (State) ({ \old(states[y_1_B2]) \with .id = (int)(z_1_B2)} ) };states == states_1_B2;
behavior B3:
assumes status == 0 && action == A3;
requires size_doActions_action3_context == 0;
ensures doActions_action3_context[0]._in.x == 0;
ensures doActions_action3_context[1]._in.x == doActions_action3_context[0].result;
ensures doActions_action3_context[2]._in.x == doActions_action3_context[1].result;
ensures \result == doActions_action3_context[2].result;
behavior B4:
assumes status != 0 && action == A3;
requires size_doActions_init_context == 0;
requires size_doActions_action1_context == 0;
ensures \result == doActions_action1_context[0].result;
*/
int doActions(N_Action action, int it);
```

# Proof #
We can then use Frama-C to prove the ACSL contract.
Once Frama-C installed, the frama-c command can be used as follows:
```

frama-c -wp -cpp-command "gcc -C -E -I . -I ." module.c
```

The results of the analysis are as follows :
```

[kernel] preprocessing with "gcc -C -E -I . -I .  module.c"
[wp] Running WP plugin...
[wp] Collecting axiomatic usage
[wp] warning: Missing RTE guards
[wp] 30 goals scheduled
[wp] [Qed] Goal typed_doActions_B0_post_part1 : Valid
[wp] [Qed] Goal typed_doActions_B0_post_part2 : Valid
[wp] [Qed] Goal typed_doActions_B0_post_2_part1 : Valid
[wp] [Qed] Goal typed_doActions_B1_post_part1 : Valid
[wp] [Qed] Goal typed_doActions_B1_post_part2 : Valid (4ms)
[wp] [Qed] Goal typed_doActions_B1_post_2_part1 : Valid
[wp] [Qed] Goal typed_doActions_B1_post_2_part2 : Valid (4ms)
[wp] [Qed] Goal typed_doActions_B1_post_3_part1 : Valid
[wp] [Qed] Goal typed_doActions_B1_post_3_part2 : Valid (4ms)
[wp] [Alt-Ergo] Goal typed_doActions_B0_post_2_part2 : Valid (8ms) (13)
[wp] [Qed] Goal typed_doActions_B1_post_4_part1 : Valid
[wp] [Qed] Goal typed_doActions_B2_post_part1 : Valid
[wp] [Qed] Goal typed_doActions_B2_post_part2 : Valid (4ms)
[wp] [Qed] Goal typed_doActions_B2_post_2_part1 : Valid
[wp] [Qed] Goal typed_doActions_B2_post_2_part2 : Valid
[wp] [Qed] Goal typed_doActions_B2_post_3_part1 : Valid
[wp] [Qed] Goal typed_doActions_B2_post_4_part1 : Valid
[wp] [Alt-Ergo] Goal typed_doActions_B1_post_4_part2 : Valid (Qed:4ms) (32ms) (47)
[wp] [Qed] Goal typed_doActions_B3_post_part1 : Valid
[wp] [Qed] Goal typed_doActions_B3_post_part2 : Valid (4ms)
[wp] [Qed] Goal typed_doActions_B3_post_2_part1 : Valid
[wp] [Alt-Ergo] Goal typed_doActions_B2_post_3_part2 : Valid (Qed:4ms) (16ms) (25)
[wp] [Qed] Goal typed_doActions_B3_post_2_part2 : Valid
[wp] [Alt-Ergo] Goal typed_doActions_B2_post_4_part2 : Valid (Qed:4ms) (44ms) (51)
[wp] [Qed] Goal typed_doActions_B3_post_3_part1 : Valid
[wp] [Qed] Goal typed_doActions_B3_post_3_part2 : Valid (4ms)
[wp] [Qed] Goal typed_doActions_B3_post_4_part1 : Valid
[wp] [Qed] Goal typed_doActions_B3_post_4_part2 : Valid (4ms)
[wp] [Qed] Goal typed_doActions_B4_post_part1 : Valid
[wp] [Qed] Goal typed_doActions_B4_post_part2 : Valid
```