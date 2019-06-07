.class public FindMaximum
.super java/lang/Object
 
.field test_arr [I

.method public <init>()V
	aload_0
	invokespecial java/lang/Object/<init>()V
	return
.end method

.method public find_maximum([I)I
.limit stack 15
.limit locals 5
ldc 1
istore 2
aload 1
ldc 0
iaload
istore 3
L0:
iload 2
aload 1
arraylength
if_icmpge    L1
aload 1
iload 2
iaload
istore 4
iload 3
iload 4
if_icmpge    L2
iload 4
istore 3
L2:
iload 2
ldc 1
iadd 
istore 2
goto    L0
L1:
iload 3
ireturn
.end method

.method public build_test_arr()I
.limit stack 19
.limit locals 1
aload_0
ldc 5
newarray int
putfield FindMaximum/test_arr [I
aload_0
getfield FindMaximum/test_arr [I
ldc 0
ldc 14
iastore
aload_0
getfield FindMaximum/test_arr [I
ldc 1
ldc 28
iastore
aload_0
getfield FindMaximum/test_arr [I
ldc 2
ldc 0
iastore
aload_0
getfield FindMaximum/test_arr [I
ldc 3
ldc 0
ldc 5
isub 
iastore
aload_0
getfield FindMaximum/test_arr [I
ldc 4
ldc 12
iastore
ldc 0
ireturn
.end method

.method public get_array()[I
.limit stack 1
.limit locals 1
aload_0
getfield FindMaximum/test_arr [I
areturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 5
.limit locals 3
new FindMaximum
dup
invokespecial FindMaximum/<init>()V
astore 2
aload 2
invokevirtual FindMaximum/build_test_arr()I
pop
aload 2
aload 2
invokevirtual FindMaximum/get_array()[I
invokevirtual FindMaximum/find_maximum([I)I
invokestatic io/println(I)V
return
.end method


