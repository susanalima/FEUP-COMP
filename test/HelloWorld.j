.class public HelloWorld
.super java/lang/Object
.method HelloWorld/build_test_arr(IILHelloWorld;)Z
invokestatic ioPlus/printHelloWorld()I
aload 0
ireturn

.method HelloWorld/build_test_arr(Z)Z
iload_1
ireturn

.method HelloWorld/main([Ljava/lang/String;)V
ldc 5
istore_2
ldc 4
istore_3
iload_2
invokestatic HelloWorld/build_test_arr(I)I
invokenonstatic HelloWorld/HelloWorld()LHelloWorld;
astore_4
aload_0
iload_2
iload_3
iload_4
invokevirtual HelloWorld/build_test_arr(IILHelloWorld;)Z
aload_0
ldc 12
ldc 15
iload_4
invokevirtual HelloWorld/build_test_arr(IILHelloWorld;)Z
aload_0
aload 0
invokevirtual HelloWorld/build_test_arr(Z)Z
invokestatic ioPlus/printHelloWorld()I
return
