package bt.ricb.ricb_api.controllers;

import bt.ricb.ricb_api.models.TemplateDto;
import bt.ricb.ricb_api.services.EmailService;
import com.lowagie.text.DocumentException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.xhtmlrenderer.pdf.ITextRenderer;

@CrossOrigin({ "*" })
@RequestMapping({ "report" })
@RestController
public class ReportController {
	@Autowired
	private TemplateEngine templateEngine;
	@Autowired
	private EmailService mailService;

	@GetMapping({ "/dtiCertificateSend" })
	public void dtiCertificateSend() throws IOException, DocumentException, MessagingException {
		String templateName = "DTICertificateTemplate";

		Context context = new Context();
		context.setVariable("policyNo", "");

		String htmlContent = this.templateEngine.process(templateName, (IContext) context);

		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(htmlContent);
		renderer.layout();

		renderer.createPDF(pdfOutputStream);
		renderer.finishPDF();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("inline", "generated.pdf");
		this.mailService.sendEmailWithAttachment("sangay.korea@gmail.com", "certificate", "Here's your certificate",
				pdfOutputStream.toByteArray());
	}

	@PostMapping({ "/dtiCertificate" })
	public ResponseEntity<byte[]> generatePdf(@RequestBody TemplateDto templateData)
			throws IOException, DocumentException, MessagingException {
		String templateName = "DTICertificateTemplate";

		Context context = new Context();
		context.setVariable("policyNo", templateData.getPolicyNo());
		context.setVariable("originalPolicy", templateData.getOriginalPolicy());
		context.setVariable("policyPeriodFrom", templateData.getPolicyPeriodFrom());
		context.setVariable("policyPeriodTo", templateData.getPolicyPeriodTo());
		context.setVariable("insured", templateData.getInsured());
		context.setVariable("customerCode", templateData.getCustomerCode());
		context.setVariable("address", templateData.getAddress());
		context.setVariable("totalSumInsured", templateData.getTotalSumInsured());
		context.setVariable("totalPremium", templateData.getTotalPremium());
		context.setVariable("totalSumInsured", templateData.getTotalSumInsured());
		context.setVariable("customer", templateData.getCustomer());
		context.setVariable("operator", templateData.getOperator());
		context.setVariable("journeyPeriodFrom", templateData.getJourneyPeriodFrom());
		context.setVariable("journeyPeriodTo", templateData.getJourneyPeriodTo());
		context.setVariable("noOfPassenger", templateData.getNoOfPassenger());
		context.setVariable("carrierType", templateData.getCarrierType());
		context.setVariable("journeyFrom", templateData.getJourneyFrom());
		context.setVariable("sumInsured", templateData.getSumInsured());
		context.setVariable("journeyTo", templateData.getJourneyTo());
		context.setVariable("premium", templateData.getPremium());
		context.setVariable("customerName", templateData.getCustomer());
		context.setVariable("citizenId", templateData.getCitizenId());
		context.setVariable("gender", templateData.getGender());

		String htmlContent = this.templateEngine.process(templateName, (IContext) context);

		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(htmlContent);
		renderer.layout();

		renderer.createPDF(pdfOutputStream);
		renderer.finishPDF();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("inline", "generated.pdf");
		this.mailService.sendEmailWithAttachment("keswan97@gmail.com", "certificate", "Here's your certificate",
				pdfOutputStream.toByteArray());

		return new ResponseEntity(pdfOutputStream.toByteArray(), (MultiValueMap) headers, HttpStatus.OK);
	}

	@PostMapping({ "/nyekorCertificate" })
	public ResponseEntity<byte[]> generateneykorPdf(@RequestBody TemplateDto templateData)
			throws IOException, DocumentException, MessagingException {
		String templateName = "nyekor";

		Context context = new Context();
		context.setVariable("policyNo", templateData.getPolicyNo());
		context.setVariable("policyPeriodFrom", templateData.getPolicyPeriodFrom());
		context.setVariable("policyPeriodTo", templateData.getPolicyPeriodTo());
		context.setVariable("policyPeriodTo", templateData.getPolicyPeriodTo());
		context.setVariable("insured", templateData.getInsured());
		context.setVariable("customerCode", templateData.getCustomerCode());
		context.setVariable("address", templateData.getAddress());
		context.setVariable("totalSumInsured", templateData.getTotalSumInsured());
		context.setVariable("totalPremium", templateData.getTotalPremium());
		context.setVariable("noOfPassenger", templateData.getNoOfPassenger());
		context.setVariable("carrierType", templateData.getCarrierType());
		context.setVariable("placeOfArrival", templateData.getPlaceOfArrival());
		context.setVariable("journeyPeriodFrom", templateData.getJourneyPeriodFrom());
		context.setVariable("journeyPeriodTo", templateData.getJourneyPeriodTo());
		context.setVariable("journeyTo", templateData.getJourneyTo());
		context.setVariable("journeyFrom", templateData.getJourneyFrom());
		context.setVariable("deathBenefit", templateData.getDeathBenefit());
		context.setVariable("emergencyBenefit", templateData.getEmergencyBenefit());
		context.setVariable("repatriationBenefit", templateData.getRepatriationBenefit());
		context.setVariable("missingBenefit", templateData.getMissingBenefit());
		context.setVariable("hospitalizationBenefit", templateData.getHospitalizationBenefit());
		context.setVariable("custometName", templateData.getCustometName());
		context.setVariable("citizenId", templateData.getCitizenId());
		context.setVariable("gender", templateData.getGender());
		context.setVariable("dob", templateData.getDob());
		context.setVariable("phoneNo", templateData.getPhoneNo());
		context.setVariable("passportNo", templateData.getPassportNo());
		context.setVariable("assured", templateData.getAssured());
		context.setVariable("premium", templateData.getPremium());

		String htmlContent = this.templateEngine.process(templateName, (IContext) context);

		ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(htmlContent);
		renderer.layout();

		renderer.createPDF(pdfOutputStream);
		renderer.finishPDF();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("inline", "generated.pdf");
		this.mailService.sendEmailWithAttachment("keswan97@gmail.com", "certificate", "Here's your certificate",
				pdfOutputStream.toByteArray());

		return new ResponseEntity(pdfOutputStream.toByteArray(), (MultiValueMap) headers, HttpStatus.OK);
	}
}
