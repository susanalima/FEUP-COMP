class HelloWorld {
	public boolean build_test_arr(int s, int b, HelloWorld a) {

		return false;
	}

	public boolean build_test_arr(boolean s) {
		return s;
	}

	public static void main(String[] args) {
		int s;
		int a;
		HelloWorld h;
		boolean a1;
		boolean b0;
		boolean b1;
		boolean b2;
		boolean b3;

		s = 5;
		a = 4;
		a1 = false;
		h = new HelloWorld();

		b0 = h.build_test_arr(a1);

		b1 = h.build_test_arr(s, a, h);
		b2 = h.build_test_arr(12, 15, h);
		b3 = h.build_test_arr(false);
	}
}