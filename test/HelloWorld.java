
class HelloWorld {
	public boolean build_test_arr(int s, int b) {

		return false;
	}

	public boolean build_test_arr(boolean s) {
		return s;
	}

	public static void main(String[] args) {
		int s;
		int a;

		s = 5;
		a = 4;

		HelloWorld h = new HelloWorld();
		h.build_test_arr(s, a);
		h.build_test_arr(12, 15);
		h.build_test_arr(false);
	}
}