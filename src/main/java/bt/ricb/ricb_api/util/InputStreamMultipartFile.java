package bt.ricb.ricb_api.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public class InputStreamMultipartFile implements MultipartFile {
	private final String name;
	private final String originalFilename;
	private final String contentType;
	private final long size;
	private final ByteArrayInputStream inputStream;

	public InputStreamMultipartFile(String name, String originalFilename, String contentType, long size,
			byte[] content) {
		this.name = name;
		this.originalFilename = originalFilename;
		this.contentType = contentType;
		this.size = size;
		this.inputStream = new ByteArrayInputStream(content);
	}

	public String getName() {
		return this.name;
	}

	public String getOriginalFilename() {
		return this.originalFilename;
	}

	public String getContentType() {
		return this.contentType;
	}

	public boolean isEmpty() {
		return (this.size == 0L);
	}

	public long getSize() {
		return this.size;
	}

	public byte[] getBytes() throws IOException {
		throw new UnsupportedOperationException("getBytes() is not supported for InputStreamMultipartFile");
	}

	public InputStream getInputStream() throws IOException {
		return this.inputStream;
	}

	public void transferTo(File dest) throws IOException, IllegalStateException {
		InputStream input = getInputStream();
		try {
			FileOutputStream output = new FileOutputStream(dest);
			try {
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = input.read(buffer)) != -1) {
					output.write(buffer, 0, bytesRead);
				}
				output.close();
			} catch (Throwable throwable) {
				try {
					output.close();
				} catch (Throwable throwable1) {
					throwable.addSuppressed(throwable1);
				}
				throw throwable;
			}
			if (input != null)
				input.close();
		} catch (Throwable throwable) {
			if (input != null)
				try {
					input.close();
				} catch (Throwable throwable1) {
					throwable.addSuppressed(throwable1);
				}
			throw throwable;
		}
	}
}