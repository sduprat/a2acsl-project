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
