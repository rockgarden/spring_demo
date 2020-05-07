package payroll;

class EmployeeNotFoundException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1662905130866968898L;

	EmployeeNotFoundException(Long id) {
		super("Could not find employee " + id);
	}
}
