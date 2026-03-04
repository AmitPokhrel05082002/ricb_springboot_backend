package bt.ricb.ricb_api.models;

public class ResponseDto {
	private Integer existStatus;
	private Integer status;
	private String responseData;
	private String message;

	public String getResponseData() {
		return this.responseData;
	}

	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}

	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getExistStatus() {
		return this.existStatus;
	}

	public void setExistStatus(Integer existStatus) {
		this.existStatus = existStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
}