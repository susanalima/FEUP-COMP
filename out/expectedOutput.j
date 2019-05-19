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
.limit locals 6
new Life
dup
invokespecial Life/<init>()V
astore 2
aload 2
invokevirtual Life/init()Z
pop
L0:
aload 2
invokevirtual Life/printField()Z
pop
aload 2
invokevirtual Life/update()Z
pop
invokestatic io/read()I
istore 3
goto    L0
.end method

.method public init()Z
.limit stack 23
.limit locals 3
ldc 1
newarray int
astore 1
aload_0
ldc 2
putfield Life/UNDERPOP_LIM I
aload_0
ldc 3
putfield Life/OVERPOP_LIM I
aload_0
ldc 3
putfield Life/REPRODUCE_NUM I
aload_0
ldc 225000
putfield Life/LOOPS_PER_MS I
aload_0
aload_0
aload 1
invokevirtual Life/fieldA([I)[I
putfield Life/fieldA [I
aload 1
ldc 0
iaload
istore 2
aload_0
iload 2
ldc 1
isub 
putfield Life/xMax I
aload_0
aload_0
getfield Life/fieldA [I
arraylength
iload 2
idiv 
ldc 1
isub 
putfield Life/yMax I
ldc 1
ireturn
.end method

.method public fieldA([I)[I
.limit stack 305
.limit locals 3
ldc 100
newarray int
astore 2
aload 1
ldc 0
ldc 10
iastore
aload 2
ldc 0
ldc 0
iastore
aload 2
ldc 1
ldc 0
iastore
aload 2
ldc 2
ldc 1
iastore
aload 2
ldc 3
ldc 0
iastore
aload 2
ldc 4
ldc 0
iastore
aload 2
ldc 5
ldc 0
iastore
aload 2
ldc 6
ldc 0
iastore
aload 2
ldc 7
ldc 0
iastore
aload 2
ldc 8
ldc 0
iastore
aload 2
ldc 9
ldc 0
iastore
aload 2
ldc 10
ldc 1
iastore
aload 2
ldc 11
ldc 0
iastore
aload 2
ldc 12
ldc 1
iastore
aload 2
ldc 13
ldc 0
iastore
aload 2
ldc 14
ldc 0
iastore
aload 2
ldc 15
ldc 0
iastore
aload 2
ldc 16
ldc 0
iastore
aload 2
ldc 17
ldc 0
iastore
aload 2
ldc 18
ldc 0
iastore
aload 2
ldc 19
ldc 0
iastore
aload 2
ldc 20
ldc 0
iastore
aload 2
ldc 21
ldc 1
iastore
aload 2
ldc 22
ldc 1
iastore
aload 2
ldc 23
ldc 0
iastore
aload 2
ldc 24
ldc 0
iastore
aload 2
ldc 25
ldc 0
iastore
aload 2
ldc 26
ldc 0
iastore
aload 2
ldc 27
ldc 0
iastore
aload 2
ldc 28
ldc 0
iastore
aload 2
ldc 29
ldc 0
iastore
aload 2
ldc 30
ldc 0
iastore
aload 2
ldc 31
ldc 0
iastore
aload 2
ldc 32
ldc 0
iastore
aload 2
ldc 33
ldc 0
iastore
aload 2
ldc 34
ldc 0
iastore
aload 2
ldc 35
ldc 0
iastore
aload 2
ldc 36
ldc 0
iastore
aload 2
ldc 37
ldc 0
iastore
aload 2
ldc 38
ldc 0
iastore
aload 2
ldc 39
ldc 0
iastore
aload 2
ldc 40
ldc 0
iastore
aload 2
ldc 41
ldc 0
iastore
aload 2
ldc 42
ldc 0
iastore
aload 2
ldc 43
ldc 0
iastore
aload 2
ldc 44
ldc 0
iastore
aload 2
ldc 45
ldc 0
iastore
aload 2
ldc 46
ldc 0
iastore
aload 2
ldc 47
ldc 0
iastore
aload 2
ldc 48
ldc 0
iastore
aload 2
ldc 49
ldc 0
iastore
aload 2
ldc 50
ldc 0
iastore
aload 2
ldc 51
ldc 0
iastore
aload 2
ldc 52
ldc 0
iastore
aload 2
ldc 53
ldc 0
iastore
aload 2
ldc 54
ldc 0
iastore
aload 2
ldc 55
ldc 0
iastore
aload 2
ldc 56
ldc 0
iastore
aload 2
ldc 57
ldc 0
iastore
aload 2
ldc 58
ldc 0
iastore
aload 2
ldc 59
ldc 0
iastore
aload 2
ldc 60
ldc 0
iastore
aload 2
ldc 61
ldc 0
iastore
aload 2
ldc 62
ldc 0
iastore
aload 2
ldc 63
ldc 0
iastore
aload 2
ldc 64
ldc 0
iastore
aload 2
ldc 65
ldc 0
iastore
aload 2
ldc 66
ldc 0
iastore
aload 2
ldc 67
ldc 0
iastore
aload 2
ldc 68
ldc 0
iastore
aload 2
ldc 69
ldc 0
iastore
aload 2
ldc 70
ldc 0
iastore
aload 2
ldc 71
ldc 0
iastore
aload 2
ldc 72
ldc 0
iastore
aload 2
ldc 73
ldc 0
iastore
aload 2
ldc 74
ldc 0
iastore
aload 2
ldc 75
ldc 0
iastore
aload 2
ldc 76
ldc 0
iastore
aload 2
ldc 77
ldc 0
iastore
aload 2
ldc 78
ldc 0
iastore
aload 2
ldc 79
ldc 0
iastore
aload 2
ldc 80
ldc 0
iastore
aload 2
ldc 81
ldc 0
iastore
aload 2
ldc 82
ldc 0
iastore
aload 2
ldc 83
ldc 0
iastore
aload 2
ldc 84
ldc 0
iastore
aload 2
ldc 85
ldc 0
iastore
aload 2
ldc 86
ldc 0
iastore
aload 2
ldc 87
ldc 0
iastore
aload 2
ldc 88
ldc 0
iastore
aload 2
ldc 89
ldc 0
iastore
aload 2
ldc 90
ldc 0
iastore
aload 2
ldc 91
ldc 0
iastore
aload 2
ldc 92
ldc 0
iastore
aload 2
ldc 93
ldc 0
iastore
aload 2
ldc 94
ldc 0
iastore
aload 2
ldc 95
ldc 0
iastore
aload 2
ldc 96
ldc 0
iastore
aload 2
ldc 97
ldc 0
iastore
aload 2
ldc 98
ldc 0
iastore
aload 2
ldc 99
ldc 0
iastore
aload 2
areturn
.end method

.method public update()Z
.limit stack 42
.limit locals 6
aload_0
getfield Life/fieldA [I
arraylength
newarray int
astore 5
ldc 0
istore 1
L0:
iload 1
aload_0
getfield Life/fieldA [I
arraylength
if_icmpge    L1
aload_0
getfield Life/fieldA [I
iload 1
iaload
istore 2
aload_0
iload 1
invokevirtual Life/getLiveNeighborN(I)I
istore 3
iload 2
ldc 1
if_icmplt    L3
aload_0
iload 3
aload_0
getfield Life/UNDERPOP_LIM I
invokevirtual Life/ge(II)Z
ifeq    L4
aload_0
iload 3
aload_0
getfield Life/OVERPOP_LIM I
invokevirtual Life/le(II)Z
ifeq    L4
iconst_1
goto    L5
L4:
iconst_0
L5:
istore 4
iload 4
ifne    L7
aload 5
iload 1
ldc 0
iastore
goto    L8
L7:
aload 5
iload 1
aload_0
getfield Life/fieldA [I
iload 1
iaload
iastore
L8:
goto    L9
L3:
aload_0
iload 3
aload_0
getfield Life/REPRODUCE_NUM I
invokevirtual Life/eq(II)Z
ifeq    L10
aload 5
iload 1
ldc 1
iastore
goto    L11
L10:
aload 5
iload 1
aload_0
getfield Life/fieldA [I
iload 1
iaload
iastore
L11:
L9:
iload 1
ldc 1
iadd 
istore 1
goto    L0
L1:
aload_0
aload 5
putfield Life/fieldA [I
ldc 1
ireturn
.end method

.method public printField()Z
.limit stack 16
.limit locals 3
ldc 0
istore 1
ldc 0
istore 2
L0:
iload 1
aload_0
getfield Life/fieldA [I
arraylength
if_icmpge    L1
aload_0
iload 2
aload_0
getfield Life/xMax I
invokevirtual Life/gt(II)Z
ifeq    L2
invokestatic io/println()V
ldc 0
istore 2
L2:
aload_0
getfield Life/fieldA [I
iload 1
iaload
invokestatic io/print(I)V
iload 1
ldc 1
iadd 
istore 1
iload 2
ldc 1
iadd 
istore 2
goto    L0
L1:
invokestatic io/println()V
invokestatic io/println()V
ldc 1
ireturn
.end method

.method public trIdx(II)I
.limit stack 4
.limit locals 3
iload 1
aload_0
getfield Life/xMax I
ldc 1
iadd 
iload 2
imul 
iadd 
ireturn
.end method

.method public cartIdx(I)[I
.limit stack 15
.limit locals 6
aload_0
getfield Life/xMax I
ldc 1
iadd 
istore 4
iload 1
iload 4
idiv 
istore 3
iload 1
iload 3
iload 4
imul 
isub 
istore 2
ldc 2
newarray int
astore 5
aload 5
ldc 0
iload 2
iastore
aload 5
ldc 1
iload 3
iastore
aload 5
areturn
.end method

.method public getNeighborCoords(I)[I
.limit stack 76
.limit locals 10
aload_0
iload 1
invokevirtual Life/cartIdx(I)[I
astore 8
aload 8
ldc 0
iaload
istore 2
aload 8
ldc 1
iaload
istore 3
iload 2
aload_0
getfield Life/xMax I
if_icmpge    L0
iload 2
ldc 1
iadd 
istore 6
aload_0
iload 2
ldc 0
invokevirtual Life/gt(II)Z
ifeq    L1
iload 2
ldc 1
isub 
istore 4
goto    L2
L1:
aload_0
getfield Life/xMax I
istore 4
L2:
goto    L3
L0:
ldc 0
istore 6
iload 2
ldc 1
isub 
istore 4
L3:
iload 3
aload_0
getfield Life/yMax I
if_icmpge    L4
iload 3
ldc 1
iadd 
istore 7
aload_0
iload 3
ldc 0
invokevirtual Life/gt(II)Z
ifeq    L5
iload 3
ldc 1
isub 
istore 5
goto    L6
L5:
aload_0
getfield Life/yMax I
istore 5
L6:
goto    L7
L4:
ldc 0
istore 7
iload 3
ldc 1
isub 
istore 5
L7:
ldc 8
newarray int
astore 9
aload 9
ldc 0
aload_0
iload 2
iload 5
invokevirtual Life/trIdx(II)I
iastore
aload 9
ldc 1
aload_0
iload 4
iload 5
invokevirtual Life/trIdx(II)I
iastore
aload 9
ldc 2
aload_0
iload 4
iload 3
invokevirtual Life/trIdx(II)I
iastore
aload 9
ldc 3
aload_0
iload 4
iload 7
invokevirtual Life/trIdx(II)I
iastore
aload 9
ldc 4
aload_0
iload 2
iload 7
invokevirtual Life/trIdx(II)I
iastore
aload 9
ldc 5
aload_0
iload 6
iload 7
invokevirtual Life/trIdx(II)I
iastore
aload 9
ldc 6
aload_0
iload 6
iload 3
invokevirtual Life/trIdx(II)I
iastore
aload 9
ldc 7
aload_0
iload 6
iload 5
invokevirtual Life/trIdx(II)I
iastore
aload 9
areturn
.end method

.method public getLiveNeighborN(I)I
.limit stack 18
.limit locals 5
ldc 0
istore 4
aload_0
iload 1
invokevirtual Life/getNeighborCoords(I)[I
astore 2
ldc 0
istore 3
L0:
iload 3
aload 2
arraylength
if_icmpge    L1
aload_0
aload_0
getfield Life/fieldA [I
aload 2
iload 3
iaload
iaload
ldc 0
invokevirtual Life/ne(II)Z
ifeq    L2
iload 4
ldc 1
iadd 
istore 4
L2:
iload 3
ldc 1
iadd 
istore 3
goto    L0
L1:
iload 4
ireturn
.end method

.method public busyWait(I)Z
.limit stack 8
.limit locals 4
iload 1
aload_0
getfield Life/LOOPS_PER_MS I
imul 
istore 3
ldc 0
istore 2
L0:
iload 2
iload 3
if_icmpge    L1
iload 2
ldc 1
iadd 
istore 2
goto    L0
L1:
ldc 1
ireturn
.end method

.method public eq(II)Z
.limit stack 6
.limit locals 3
aload_0
iload 1
iload 2
invokevirtual Life/lt(II)Z
ifne    L0
aload_0
iload 2
iload 1
invokevirtual Life/lt(II)Z
ifne    L0
iconst_1
goto    L1
L0:
iconst_0
L1:
ireturn
.end method

.method public ne(II)Z
.limit stack 3
.limit locals 3
aload_0
iload 1
iload 2
invokevirtual Life/eq(II)Z
ifne L0
iconst_1
goto L1
L0:
iconst_0
L1:
ireturn
.end method

.method public lt(II)Z
.limit stack 2
.limit locals 3
iload 1
iload 2
if_icmpge  L0
iconst_1
goto    L1
L0:
iconst_0
L1:
ireturn
.end method

.method public le(II)Z
.limit stack 6
.limit locals 3
aload_0
iload 1
iload 2
invokevirtual Life/lt(II)Z
ifne    L3
aload_0
iload 1
iload 2
invokevirtual Life/eq(II)Z
ifeq    L0
L3:
iconst_1
goto    L1
L0:
iconst_0
L1:
ireturn
.end method

.method public gt(II)Z
.limit stack 3
.limit locals 3
aload_0
iload 1
iload 2
invokevirtual Life/le(II)Z
ifne L0
iconst_1
goto L1
L0:
iconst_0
L1:
ireturn
.end method

.method public ge(II)Z
.limit stack 6
.limit locals 3
aload_0
iload 1
iload 2
invokevirtual Life/gt(II)Z
ifne    L3
aload_0
iload 1
iload 2
invokevirtual Life/eq(II)Z
ifeq    L0
L3:
iconst_1
goto    L1
L0:
iconst_0
L1:
ireturn
.end method


