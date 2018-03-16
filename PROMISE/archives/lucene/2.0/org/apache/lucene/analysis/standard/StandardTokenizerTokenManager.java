package org.apache.lucene.analysis.standard;
import java.io.*;

public class StandardTokenizerTokenManager implements StandardTokenizerConstants
{
  public  java.io.PrintStream debugStream = System.out;
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjMoveStringLiteralDfa0_0()
{
   return jjMoveNfa_0(0, 0);
}
private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0x1ff0000000000000L, 0xffffffffffffc000L, 0xffffffffL, 0x600000000000000L
};
static final long[] jjbitVec2 = {
   0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static final long[] jjbitVec3 = {
   0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffL, 0x0L
};
static final long[] jjbitVec4 = {
   0xffffffffffffffffL, 0xffffffffffffffffL, 0x0L, 0x0L
};
static final long[] jjbitVec5 = {
   0x3fffffffffffL, 0x0L, 0x0L, 0x0L
};
static final long[] jjbitVec6 = {
   0x0L, 0x0L, 0xfffff00000000000L, 0x7fffffL
};
static final long[] jjbitVec7 = {
   0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffL, 0x0L
};
static final long[] jjbitVec8 = {
   0xfffffffeL, 0x0L, 0x0L, 0x0L
};
static final long[] jjbitVec9 = {
   0x0L, 0x0L, 0x0L, 0xff7fffffff7fffffL
};
static final long[] jjbitVec10 = {
   0x1600L, 0x0L, 0x0L, 0x0L
};
static final long[] jjbitVec11 = {
   0x0L, 0xffc000000000L, 0x0L, 0xffc000000000L
};
static final long[] jjbitVec12 = {
   0x0L, 0x3ff00000000L, 0x0L, 0x3ff000000000000L
};
static final long[] jjbitVec13 = {
   0x0L, 0xffc000000000L, 0x0L, 0xff8000000000L
};
static final long[] jjbitVec14 = {
   0x0L, 0xffc000000000L, 0x0L, 0x0L
};
static final long[] jjbitVec15 = {
   0x0L, 0x3ff0000L, 0x0L, 0x3ff0000L
};
static final long[] jjbitVec16 = {
   0x0L, 0x3ffL, 0x0L, 0x0L
};
static final long[] jjbitVec17 = {
   0xfffffffeL, 0x0L, 0xfffff00000000000L, 0x7fffffL
};
private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 75;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 1)
                        kind = 1;
                     jjCheckNAddStates(0, 11);
                  }
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(12, 17);
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(18, 23);
                  break;
               case 2:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(18, 23);
                  break;
               case 3:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(3, 4);
                  break;
               case 4:
               case 5:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(5, 6);
                  break;
               case 6:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAdd(7);
                  break;
               case 7:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAdd(7);
                  break;
               case 8:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(8, 9);
                  break;
               case 9:
               case 10:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(10, 11);
                  break;
               case 11:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAdd(12);
                  break;
               case 12:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(12, 13);
                  break;
               case 13:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAddTwoStates(14, 15);
                  break;
               case 14:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(14, 15);
                  break;
               case 15:
               case 16:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(11, 16);
                  break;
               case 17:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(17, 18);
                  break;
               case 18:
               case 19:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(19, 20);
                  break;
               case 20:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAdd(21);
                  break;
               case 21:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(21, 22);
                  break;
               case 22:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAddTwoStates(23, 24);
                  break;
               case 23:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(23, 24);
                  break;
               case 24:
               case 25:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(25, 26);
                  break;
               case 26:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAdd(27);
                  break;
               case 27:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(22, 27);
                  break;
               case 28:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(12, 17);
                  break;
               case 29:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(0, 11);
                  break;
               case 30:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAdd(30);
                  break;
               case 31:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(24, 26);
                  break;
               case 32:
                  if ((0x600000000000L & l) != 0L)
                     jjCheckNAdd(33);
                  break;
               case 33:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(27, 29);
                  break;
               case 35:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(35, 36);
                  break;
               case 36:
                  if ((0x600000000000L & l) != 0L)
                     jjCheckNAdd(37);
                  break;
               case 37:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 5)
                     kind = 5;
                  jjCheckNAddTwoStates(36, 37);
                  break;
               case 38:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(38, 39);
                  break;
               case 39:
                  if (curChar == 46)
                     jjCheckNAdd(40);
                  break;
               case 40:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAddTwoStates(39, 40);
                  break;
               case 41:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(41, 42);
                  break;
               case 42:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAddTwoStates(43, 44);
                  break;
               case 43:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(43, 44);
                  break;
               case 44:
               case 45:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAdd(45);
                  break;
               case 46:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(46, 47);
                  break;
               case 47:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAddTwoStates(48, 49);
                  break;
               case 48:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(48, 49);
                  break;
               case 49:
               case 50:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(50, 51);
                  break;
               case 51:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAdd(52);
                  break;
               case 52:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(47, 52);
                  break;
               case 53:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(53, 54);
                  break;
               case 54:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAddTwoStates(55, 56);
                  break;
               case 55:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(55, 56);
                  break;
               case 56:
               case 57:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(57, 58);
                  break;
               case 58:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAdd(59);
                  break;
               case 59:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(59, 60);
                  break;
               case 60:
                  if ((0xf00000000000L & l) != 0L)
                     jjCheckNAddTwoStates(61, 62);
                  break;
               case 61:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(61, 62);
                  break;
               case 62:
               case 63:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(58, 63);
                  break;
               case 66:
                  if (curChar == 39)
                     jjstateSet[jjnewStateCnt++] = 67;
                  break;
               case 69:
                  if (curChar == 46)
                     jjCheckNAdd(70);
                  break;
               case 71:
                  if (curChar != 46)
                     break;
                  if (kind > 3)
                     kind = 3;
                  jjCheckNAdd(70);
                  break;
               case 73:
                  if (curChar == 38)
                     jjstateSet[jjnewStateCnt++] = 74;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(30, 35);
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 1)
                        kind = 1;
                     jjCheckNAddStates(0, 11);
                  }
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(18, 23);
                  break;
               case 2:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(18, 23);
                  break;
               case 3:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(3, 4);
                  break;
               case 5:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjAddStates(36, 37);
                  break;
               case 6:
                  if (curChar == 95)
                     jjCheckNAdd(7);
                  break;
               case 7:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAdd(7);
                  break;
               case 8:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(8, 9);
                  break;
               case 10:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(10, 11);
                  break;
               case 11:
                  if (curChar == 95)
                     jjCheckNAdd(12);
                  break;
               case 12:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(12, 13);
                  break;
               case 13:
                  if (curChar == 95)
                     jjCheckNAddTwoStates(14, 15);
                  break;
               case 14:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(14, 15);
                  break;
               case 16:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(11, 16);
                  break;
               case 17:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(17, 18);
                  break;
               case 19:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjAddStates(38, 39);
                  break;
               case 20:
                  if (curChar == 95)
                     jjCheckNAdd(21);
                  break;
               case 21:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(21, 22);
                  break;
               case 22:
                  if (curChar == 95)
                     jjCheckNAddTwoStates(23, 24);
                  break;
               case 23:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(23, 24);
                  break;
               case 25:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjAddStates(40, 41);
                  break;
               case 26:
                  if (curChar == 95)
                     jjCheckNAdd(27);
                  break;
               case 27:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(22, 27);
                  break;
               case 29:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(0, 11);
                  break;
               case 30:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAdd(30);
                  break;
               case 31:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(24, 26);
                  break;
               case 32:
                  if (curChar == 95)
                     jjCheckNAdd(33);
                  break;
               case 33:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(27, 29);
                  break;
               case 34:
                  if (curChar == 64)
                     jjCheckNAdd(35);
                  break;
               case 35:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(35, 36);
                  break;
               case 37:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 5)
                     kind = 5;
                  jjCheckNAddTwoStates(36, 37);
                  break;
               case 38:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(38, 39);
                  break;
               case 40:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAddTwoStates(39, 40);
                  break;
               case 41:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(41, 42);
                  break;
               case 42:
                  if (curChar == 95)
                     jjCheckNAddTwoStates(43, 44);
                  break;
               case 43:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(43, 44);
                  break;
               case 45:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjstateSet[jjnewStateCnt++] = 45;
                  break;
               case 46:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(46, 47);
                  break;
               case 47:
                  if (curChar == 95)
                     jjCheckNAddTwoStates(48, 49);
                  break;
               case 48:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(48, 49);
                  break;
               case 50:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjAddStates(42, 43);
                  break;
               case 51:
                  if (curChar == 95)
                     jjCheckNAdd(52);
                  break;
               case 52:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(47, 52);
                  break;
               case 53:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(53, 54);
                  break;
               case 54:
                  if (curChar == 95)
                     jjCheckNAddTwoStates(55, 56);
                  break;
               case 55:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(55, 56);
                  break;
               case 57:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(57, 58);
                  break;
               case 58:
                  if (curChar == 95)
                     jjCheckNAdd(59);
                  break;
               case 59:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(59, 60);
                  break;
               case 60:
                  if (curChar == 95)
                     jjCheckNAddTwoStates(61, 62);
                  break;
               case 61:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(61, 62);
                  break;
               case 63:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(58, 63);
                  break;
               case 64:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddStates(30, 35);
                  break;
               case 65:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(65, 66);
                  break;
               case 67:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 2)
                     kind = 2;
                  jjCheckNAddTwoStates(66, 67);
                  break;
               case 68:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(68, 69);
                  break;
               case 70:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjAddStates(44, 45);
                  break;
               case 72:
                  if ((0x7fffffe07fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(72, 73);
                  break;
               case 73:
                  if (curChar == 64)
                     jjCheckNAdd(74);
                  break;
               case 74:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 4)
                     kind = 4;
                  jjCheckNAdd(74);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (int)(curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                  {
                     if (kind > 12)
                        kind = 12;
                  }
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2))
                  {
                     if (kind > 13)
                        kind = 13;
                  }
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(18, 23);
                  if (jjCanMove_3(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(12, 17);
                  if (jjCanMove_4(hiByte, i1, i2, l1, l2))
                  {
                     if (kind > 1)
                        kind = 1;
                     jjCheckNAddStates(0, 11);
                  }
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(30, 35);
                  break;
               case 1:
                  if (jjCanMove_1(hiByte, i1, i2, l1, l2) && kind > 13)
                     kind = 13;
                  break;
               case 2:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(18, 23);
                  break;
               case 3:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(3, 4);
                  break;
               case 4:
                  if (jjCanMove_3(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(5, 6);
                  break;
               case 5:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(5, 6);
                  break;
               case 7:
                  if (!jjCanMove_4(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjstateSet[jjnewStateCnt++] = 7;
                  break;
               case 8:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(8, 9);
                  break;
               case 9:
                  if (jjCanMove_3(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(10, 11);
                  break;
               case 10:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(10, 11);
                  break;
               case 12:
                  if (jjCanMove_4(hiByte, i1, i2, l1, l2))
                     jjAddStates(46, 47);
                  break;
               case 14:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjAddStates(48, 49);
                  break;
               case 15:
                  if (!jjCanMove_3(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(11, 16);
                  break;
               case 16:
                  if (!jjCanMove_2(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(11, 16);
                  break;
               case 17:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(17, 18);
                  break;
               case 18:
                  if (jjCanMove_3(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(19, 20);
                  break;
               case 19:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(19, 20);
                  break;
               case 21:
                  if (jjCanMove_4(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(21, 22);
                  break;
               case 23:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjAddStates(50, 51);
                  break;
               case 24:
                  if (jjCanMove_3(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(25, 26);
                  break;
               case 25:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(25, 26);
                  break;
               case 27:
                  if (!jjCanMove_4(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(22, 27);
                  break;
               case 28:
                  if (jjCanMove_3(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(12, 17);
                  break;
               case 29:
                  if (!jjCanMove_4(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAddStates(0, 11);
                  break;
               case 30:
                  if (!jjCanMove_4(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 1)
                     kind = 1;
                  jjCheckNAdd(30);
                  break;
               case 31:
                  if (jjCanMove_4(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(24, 26);
                  break;
               case 33:
                  if (jjCanMove_4(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(27, 29);
                  break;
               case 35:
                  if (jjCanMove_4(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(35, 36);
                  break;
               case 37:
                  if (!jjCanMove_4(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 5)
                     kind = 5;
                  jjCheckNAddTwoStates(36, 37);
                  break;
               case 38:
                  if (jjCanMove_4(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(38, 39);
                  break;
               case 40:
                  if (!jjCanMove_4(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAddTwoStates(39, 40);
                  break;
               case 41:
                  if (jjCanMove_4(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(41, 42);
                  break;
               case 43:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjAddStates(52, 53);
                  break;
               case 44:
                  if (!jjCanMove_3(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAdd(45);
                  break;
               case 45:
                  if (!jjCanMove_2(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAdd(45);
                  break;
               case 46:
                  if (jjCanMove_4(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(46, 47);
                  break;
               case 48:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjAddStates(54, 55);
                  break;
               case 49:
                  if (jjCanMove_3(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(50, 51);
                  break;
               case 50:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(50, 51);
                  break;
               case 52:
                  if (!jjCanMove_4(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(47, 52);
                  break;
               case 53:
                  if (jjCanMove_4(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(53, 54);
                  break;
               case 55:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjAddStates(56, 57);
                  break;
               case 56:
                  if (jjCanMove_3(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(57, 58);
                  break;
               case 57:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(57, 58);
                  break;
               case 59:
                  if (jjCanMove_4(hiByte, i1, i2, l1, l2))
                     jjAddStates(58, 59);
                  break;
               case 61:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjAddStates(60, 61);
                  break;
               case 62:
                  if (!jjCanMove_3(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(58, 63);
                  break;
               case 63:
                  if (!jjCanMove_2(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 7)
                     kind = 7;
                  jjCheckNAddTwoStates(58, 63);
                  break;
               case 64:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(30, 35);
                  break;
               case 65:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(65, 66);
                  break;
               case 67:
                  if (!jjCanMove_2(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 2)
                     kind = 2;
                  jjCheckNAddTwoStates(66, 67);
                  break;
               case 68:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(68, 69);
                  break;
               case 70:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjAddStates(44, 45);
                  break;
               case 72:
                  if (jjCanMove_2(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(72, 73);
                  break;
               case 74:
                  if (!jjCanMove_2(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 4)
                     kind = 4;
                  jjstateSet[jjnewStateCnt++] = 74;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 75 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   30, 31, 32, 34, 38, 39, 41, 42, 46, 47, 53, 54, 5, 6, 10, 11, 
   19, 20, 3, 4, 8, 9, 17, 18, 31, 32, 34, 32, 33, 34, 65, 66, 
   68, 69, 72, 73, 5, 6, 19, 20, 25, 26, 50, 51, 70, 71, 12, 13, 
   14, 15, 23, 24, 43, 44, 48, 49, 55, 56, 59, 60, 61, 62, 
};
private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 48:
         return ((jjbitVec2[i2] & l2) != 0L);
      case 49:
         return ((jjbitVec3[i2] & l2) != 0L);
      case 51:
         return ((jjbitVec4[i2] & l2) != 0L);
      case 61:
         return ((jjbitVec5[i2] & l2) != 0L);
      default : 
         if ((jjbitVec0[i1] & l1) != 0L)
            return true;
         return false;
   }
}
private static final boolean jjCanMove_1(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 215:
         return ((jjbitVec7[i2] & l2) != 0L);
      default : 
         if ((jjbitVec6[i1] & l1) != 0L)
            return true;
         return false;
   }
}
private static final boolean jjCanMove_2(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 0:
         return ((jjbitVec9[i2] & l2) != 0L);
      default : 
         if ((jjbitVec8[i1] & l1) != 0L)
            return true;
         return false;
   }
}
private static final boolean jjCanMove_3(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 6:
         return ((jjbitVec12[i2] & l2) != 0L);
      case 11:
         return ((jjbitVec13[i2] & l2) != 0L);
      case 13:
         return ((jjbitVec14[i2] & l2) != 0L);
      case 14:
         return ((jjbitVec15[i2] & l2) != 0L);
      case 16:
         return ((jjbitVec16[i2] & l2) != 0L);
      default : 
         if ((jjbitVec10[i1] & l1) != 0L)
            if ((jjbitVec11[i2] & l2) == 0L)
               return false;
            else
            return true;
         return false;
   }
}
private static final boolean jjCanMove_4(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 0:
         return ((jjbitVec9[i2] & l2) != 0L);
      case 215:
         return ((jjbitVec7[i2] & l2) != 0L);
      default : 
         if ((jjbitVec17[i1] & l1) != 0L)
            return true;
         return false;
   }
}
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, null, null, null, null, 
null, null, null, };
public static final String[] lexStateNames = {
   "DEFAULT", 
};
static final long[] jjtoToken = {
   0x30ffL, 
};
static final long[] jjtoSkip = {
   0x8000L, 
};
protected CharStream input_stream;
private final int[] jjrounds = new int[75];
private final int[] jjstateSet = new int[150];
protected char curChar;
public StandardTokenizerTokenManager(CharStream stream)
{
   input_stream = stream;
}
public StandardTokenizerTokenManager(CharStream stream, int lexState)
{
   this(stream);
   SwitchTo(lexState);
}
public void ReInit(CharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 75; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
public void ReInit(CharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

public Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedPos == 0 && jjmatchedKind > 15)
   {
      jjmatchedKind = 15;
   }
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

}
