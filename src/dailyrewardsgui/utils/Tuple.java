package dailyrewardsgui.utils;

public class Tuple<T1, T2> {

	public static <T1, T2> Tuple<T1, T2> of(T1 t1, T2 t2) {
		return new Tuple<>(t1, t2);
	}

	private T1 t1;
	private T2 t2;
	protected Tuple(T1 t1, T2 t2) {
		this.t1 = t1;
		this.t2 = t2;
	}

	public T1 getObj1() {
		return t1;
	}

	public void setObj1(T1 t1) {
		this.t1 = t1;
	}

	public T2 getObj2() {
		return t2;
	}

	public void setObj2(T2 t2) {
		this.t2 = t2;
	}

}