.class public Life
.super java/lang/Object
 
.field UNDERPOP_LIM I

.field OVERPOP_LIM I

.field REPRODUCE_NUM I

.field LOOPS_PER_MS I

.field xMax I

.field yMax I

.field fieldA [I

.method public <init>()V
	aload_0
	invokespecial java/lang/Object/<init>()V
	return
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 5
.limit locals 4
new Life
dup
invokespecial Life/<init>()V
astore 2
aload 2
invokevirtual Life/init()Z
L0:
aload 2
invokevirtual Life/printField()Z
aload 2
invokevirtual Life/update()Z
invokestatic io/read()I
istore 3
goto    L0

.end method