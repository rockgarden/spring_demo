package com.example.uploadingfiles.storage;

public class StorageException extends RuntimeException {

	/**
	 * The serializable class StorageException does not declare a static final
	 * serialVersionUID field of type longJava(536871008)
	 */
	private static final long serialVersionUID = -5197833330671071039L;

	public StorageException(String message) {
		super(message);
	}

	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}
}
