class Lazysort extends Quicksort {
  Lazysort();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method Quicksort."<init>":()V
       4: return

  public static void main(java.lang.String[]);
    Code:
       0: bipush        10
       2: newarray       int
       4: astore_1
       5: iconst_0
       6: istore_2
       7: iload_2
       8: aload_1
       9: arraylength
      10: if_icmpge     27
      13: aload_1
      14: iload_2
      15: aload_1
      16: arraylength
      17: iload_2
      18: isub
      19: iastore
      20: iload_2
      21: iconst_1
      22: iadd
      23: istore_2
      24: goto          7
      27: new           #2                  // class Lazysort
      30: dup
      31: invokespecial #3                  // Method "<init>":()V
      34: astore        4
      36: aload         4
      38: aload_1
      39: invokevirtual #4                  // Method Quicksort.quicksort:([I)Z
      42: pop
      43: aload         4
      45: aload_1
      46: invokevirtual #5                  // Method Quicksort.printL:([I)Z
      49: istore_3
      50: return

  public boolean quicksort(int[]);
    Code:
       0: iconst_0
       1: iconst_5
       2: invokestatic  #6                  // Method MathUtils.random:(II)I
       5: iconst_4
       6: if_icmpge     20
       9: aload_0
      10: aload_1
      11: invokevirtual #7                  // Method beLazy:([I)Z
      14: pop
      15: iconst_1
      16: istore_2
      17: goto          22
      20: iconst_0
      21: istore_2
      22: iload_2
      23: ifeq          39
      26: iload_2
      27: ifne          34
      30: iconst_1
      31: goto          35
      34: iconst_0
      35: istore_2
      36: goto          50
      39: aload_0
      40: aload_1
      41: iconst_0
      42: aload_1
      43: arraylength
      44: iconst_1
      45: isub
      46: invokevirtual #8                  // Method quicksort:([III)Z
      49: istore_2
      50: iload_2
      51: ireturn

  public boolean beLazy(int[]);
    Code:
       0: aload_1
       1: arraylength
       2: istore_2
       3: iconst_0
       4: istore_3
       5: iload_3
       6: iload_2
       7: iconst_2
       8: idiv
       9: if_icmpge     30
      12: aload_1
      13: iload_3
      14: iconst_0
      15: bipush        10
      17: invokestatic  #6                  // Method MathUtils.random:(II)I
      20: iconst_1
      21: iadd
      22: iastore
      23: iload_3
      24: iconst_1
      25: iadd
      26: istore_3
      27: goto          5
      30: iload_3
      31: iload_2
      32: if_icmpge     53
      35: aload_1
      36: iload_3
      37: iconst_0
      38: bipush        10
      40: invokestatic  #6                  // Method MathUtils.random:(II)I
      43: iconst_1
      44: iadd
      45: iastore
      46: iload_3
      47: iconst_1
      48: iadd
      49: istore_3
      50: goto          30
      53: iconst_1
      54: ireturn
}