package dailyrewardsgui.utils;

public class Builder<T> {

	public static <T> Builder<T> create(T t) {
		return new Builder<>(t);
	}

	private final T t;
	protected Builder(T t) {
		this.t = t;
	}

	public Builder<T> invoke(Builder.Invoke<T> invoke) {
		invoke.invoke(t);
		return this;
	}

	public T build() {
		return t;
	}

	@FunctionalInterface
	public static interface Invoke<IT> {
		public void invoke(IT val);
	}

}