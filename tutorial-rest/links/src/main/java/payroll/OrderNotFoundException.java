package payroll;

class OrderNotFoundException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 5264080103290802110L;

	OrderNotFoundException(Long id) {
		super("Could not find order " + id);
	}
}
