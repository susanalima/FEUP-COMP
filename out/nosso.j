.class public Lazysort
.super Quicksort
 
.method public <init>()V
	aload_0
	invokespecial Quicksort/<init>()V
	return
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 16
.limit locals 6
ldc 10
newarray int
astore 2
ldc 0
istore 3
L0:
iload 3
aload 2
arraylength
if_icmpge    L1
aload 2
iload 3
aload 2
arraylength
iload 3
isub 
iastore
iload 3
ldc 1
iadd 
istore 3
goto    L0
L1:
new Lazysort
dup
invokespecial Lazysort/<init>()V
astore 5
aload 5
aload 2
invokevirtual Quicksort/quicksort([I)Z
pop
aload 5
aload 2
invokevirtual Quicksort/printL([I)Z
istore 4
return
.end method

.method public quicksort([I)Z
.limit stack 15
.limit locals 3
ldc 0
ldc 5
invokestatic MathUtils/random(II)I
ldc 4
if_icmpge    L0
aload_0
aload 1
invokevirtual Lazysort/beLazy([I)Z
pop
ldc 1
istore 2
goto    L1
L0:
ldc 0
istore 2
L1:
iload 2
ifeq    L2
iload 2
ifne L3
iconst_1
goto    L4
L3:
iconst_0
L4:
istore 2
goto    L5
L2:
aload_0
aload 1
ldc 0
aload 1
arraylength
ldc 1
isub 
invokevirtual Lazysort/quicksort([III)Z
istore 2
L5:
iload 2
ireturn
.end method

.method public beLazy([I)Z
.limit stack 22
.limit locals 4
aload 1
arraylength
istore 2
ldc 0
istore 3
L0:
iload 3
iload 2
ldc 2
idiv 
if_icmpge    L1
aload 1
iload 3
ldc 0
ldc 10
invokestatic MathUtils/random(II)I
ldc 1
iadd 
iastore
iload 3
ldc 1
iadd 
istore 3
goto    L0
L1:
L2:
iload 3
iload 2
if_icmpge    L3
aload 1
iload 3
ldc 0
ldc 10
invokestatic MathUtils/random(II)I
ldc 1
iadd 
iastore
iload 3
ldc 1
iadd 
istore 3
goto    L2
L3:
ldc 1
ireturn
.end method


