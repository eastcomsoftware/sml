package com.eastom_sw.sml.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class SerializationUtils {
	public static Object clone(Serializable object) {
		return deserialize(serialize(object));
	}

	public static void serialize(Serializable obj, OutputStream outputStream) {
		if (outputStream == null) {
			throw new IllegalArgumentException(
					"The OutputStream must not be null");
		}
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(outputStream);
			out.writeObject(obj);
		} catch (Exception ex) {

		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException ex) {
			}
		}
	}

	public static byte[] serialize(Serializable obj) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
		serialize(obj, baos);
		return baos.toByteArray();
	}

	public static Object deserialize(InputStream inputStream) {
		if (inputStream == null) {
			throw new IllegalArgumentException(
					"The InputStream must not be null");
		}
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(inputStream);
			return in.readObject();
		} catch (ClassNotFoundException ex) {

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException ex) {
			}
		}
		return in;
	}

	public static Object deserialize(byte[] objectData) {
		if (objectData == null) {
			throw new IllegalArgumentException("The byte[] must not be null");
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(objectData);
		return deserialize(bais);
	}
}
