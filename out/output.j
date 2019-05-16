.class public MonteCarloPi
.super java/lang/Object
 
.method public <init>()V
	aload_0
	invokespecial java/lang/Object/<init>()V
	return
.end method

.method public performSingleEstimate()Z
.limit stack 32 
.limit locals 5
ldc 1
istore 1
ldc 2
istore 2
iload 1
iload 1
imul 
iload 2
iload 2
imul 
iadd 
ldc 100
idiv 
istore 4
iload 4
ldc 100
if_icmpge    L0
ldc 1
istore 3
goto    L1
L0:
ldc 0
istore 3
L1:
iload 3
ireturn
.end method

.method public estimatePi100(I)I
.limit stack 32 
.limit locals 5
ldc 0
istore 3
ldc 0
istore 2
L0:
iload 3
iload 1
if_icmpge    L1
aload_0
invokevirtual MonteCarloPi/performSingleEstimate()Z
ifeq    L2
iload 2
ldc 1
iadd 
istore 2
L2:
iload 3
ldc 1
iadd 
istore 3
goto    L0
L1:
ldc 400
iload 2
imul 
iload 1
idiv 
istore 4
iload 4
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 32 
.limit locals 4
ldc 2
istore 3
new MonteCarloPi
dup
invokespecial MonteCarloPi/<init>()V
iload 3
invokevirtual MonteCarloPi/estimatePi100(I)I
istore 2
iload 2
invokestatic io/println(I)V
return
.end method


