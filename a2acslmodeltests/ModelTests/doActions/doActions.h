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
	ensures \let z_1_B1=doActions_action2_context[0]._out.z;\let states_1_B1=IteratorExp;\result == (states_1_B1[0]).id;
	ensures \let z_1_B1=doActions_action2_context[0]._out.z;\let states_1_B1=IteratorExp;states == states_1_B1;
behavior B2:
	assumes action == A2;
	assumes \let y_1_B2=doActions_action2_context[0]._out.y;y_1_B2 != 0;
	requires size_doActions_action2_context == 0;
	ensures doActions_action2_context[0]._in.z == 0;
	ensures doActions_action2_context[0]._in.x == it;
	ensures \let y_1_B2=doActions_action2_context[0]._out.y;\let z_1_B2=doActions_action2_context[0]._out.z;\let states_1_B2=IteratorExp;\result == (states_1_B2[0]).id;
	ensures \let y_1_B2=doActions_action2_context[0]._out.y;\let z_1_B2=doActions_action2_context[0]._out.z;\let states_1_B2=IteratorExp;states == states_1_B2;
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


