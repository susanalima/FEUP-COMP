.class public HelloWorld
.super java/lang/Object
 
.method public <init>()V
	aload_0
	invokespecial java/lang/Object/<init>()V
	return
.end method

.method HelloWorld/build_test_arr(IILHelloWorld;)Z
invokestatic ioPlus/printHelloWorld()I
aload 0
ireturn
.end method

.method HelloWorld/build_test_arr(Z)Z
iload_1
ireturn
.end method

.method HelloWorld/main([Ljava/lang/String;)V
ldc 5
istore_2
ldc 4
istore_3
aload 0
astore_5
new HelloWorld
dup
invokespecial HelloWorld/<init>()LHelloWorld;
astore_4
iload_5
invokevirtual HelloWorld/build_test_arr(Z)Z
astore_6
iload_2
iload_3
iload_4
invokevirtual HelloWorld/build_test_arr(IILHelloWorld;)Z
astore_7
ldc 12
ldc 15
iload_4
invokevirtual HelloWorld/build_test_arr(IILHelloWorld;)Z
astore_8
aload 0
invokevirtual HelloWorld/build_test_arr(Z)Z
astore_9
invokestatic ioPlus/printHelloWorld()I
return
.end method


