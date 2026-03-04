package bt.ricb.ricb_api.controllers;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import bt.ricb.ricb_api.dao.*;
import bt.ricb.ricb_api.util.BFSPKIImplementation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
@RestController
@RequestMapping({ "ricb-business" })
@CrossOrigin({ "*" })
public class business {

	@Autowired
	private ricbDAO ricbDAO;
	
	@GetMapping("/resetPin")
	public ResponseEntity<?> resetPin(
	        @RequestParam(required = false) String cidNo,
	        @RequestParam(required = false) String newPIN) {
	    try {
	        if (cidNo == null || newPIN == null) {
	            return ResponseEntity.badRequest().body("Error: please provide both cidNo and newPIN.");
	        }

	        JSONArray json = ricbDAO.resetPin(cidNo, newPIN);
	        JSONObject wrapper = new JSONObject();
	        wrapper.put("data", json.toString()); // wrap array as string
	        return ResponseEntity.ok(wrapper.toString());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.internalServerError().body("Server was not able to process your request");
	    }
	}


	
	@GetMapping("/fireSFdetails")
    public ResponseEntity<?> fireSFdetails(@RequestParam(required = false) String policyNo) {
      try {
        if (policyNo == null) {
          return ResponseEntity.badRequest().body("Error: please enter Policy number.");
        }
        JSONArray json = ricbDAO.getInstance().fireSFdetails(policyNo);
        return ResponseEntity.ok(json.toString());
      } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body("Server was not able to process your request");
      }
    }
	
	@GetMapping("/generalactivepolicy")

	public ResponseEntity<?> generalactivepolicy(@RequestParam(required = false) String cidNo) {

	try {

	if (cidNo == null) {

	return ResponseEntity.badRequest().body("Error: please enter cid number of a policy holder.");

	}

	JSONArray json = ricbDAO.getPolicyDetails(cidNo, "generalactivepolicy");

	return ResponseEntity.ok(json.toString());

	} catch (Exception e) {

	e.printStackTrace();

	return ResponseEntity.internalServerError().body("Server was not able to process your request");

	}

	}
	
	@GetMapping("/lifeactivepolicy")

	public ResponseEntity<?> lifeactivepolicy(@RequestParam(required = false) String cidNo) {

	try {

	if (cidNo == null) {

	return ResponseEntity.badRequest().body("Error: please enter cid number of a policy holder.");

	}

	JSONArray json = ricbDAO.getPolicyDetails(cidNo, "lifeactivepolicy");

	return ResponseEntity.ok(json.toString());

	} catch (Exception e) {

	e.printStackTrace();

	return ResponseEntity.internalServerError().body("Server was not able to process your request");

	}

	}

	@GetMapping("/generalinsurance")
	public ResponseEntity<?> policyNumber(@RequestParam(required = false) String cidNo) {
		try {
			if (cidNo == null) {
				return ResponseEntity.badRequest().body("Error: please enter cid number of a policy holder.");
			}
			JSONArray json = ricbDAO.getPolicyDetails(cidNo, "lifeinsurance");
			return ResponseEntity.ok(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

    @GetMapping("/generalinsurancedetails")
    public ResponseEntity<?> policyDetails(@RequestParam(required = false) String policyNo) {
        try {
            if (policyNo == null) {
                return ResponseEntity.badRequest().body("Error: please enter policy number.");
            }
            JSONArray json = ricbDAO.getAllPolicyDetails(policyNo);
            return ResponseEntity.ok(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Server was not able to process your request");
        }
    }

	@GetMapping("/adduserdetails")
	public ResponseEntity<?> addUserDetails(@RequestParam String userName, @RequestParam String password,
			@RequestParam String phoneNo, @RequestParam String email, @RequestParam String cidNumber,
			@RequestParam String otp) {
		try {
			if (userName == null) {
				return ResponseEntity.badRequest().body("Error: please enter user details.");
			}
			JSONArray json = ricbDAO.insertUserDetails(userName, password, phoneNo, email, cidNumber, otp);
			return ResponseEntity.ok(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

	@GetMapping("/verifyotp")
	public ResponseEntity<?> verifyotp(@RequestParam(required = false) String otp,
			@RequestParam(required = false) String cid) {
		try {
			if (otp == null) {
				return ResponseEntity.badRequest().body("Error: please enter otp.");
			}
			JSONArray json = ricbDAO.validateOTP(otp, cid);
			return ResponseEntity.ok(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

	@GetMapping("/validatePassword")
	public ResponseEntity<?> validatePassword(@RequestParam(required = false) String cidNo,
	        @RequestParam(required = false) String password) {
	    try {
	        // Validate both parameters
	        if (cidNo == null || cidNo.trim().isEmpty()) {
	            return ResponseEntity.badRequest().body("Error: please enter CID number.");
	        }
	        if (password == null || password.trim().isEmpty()) {
	            return ResponseEntity.badRequest().body("Error: please enter password.");
	        }
	        
	        JSONArray json = ricbDAO.validatePassword(cidNo.trim(), password);
	        return ResponseEntity.ok(json.toString());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.internalServerError().body("Server was not able to process your request");
	    }
	}

	@GetMapping("/creditinvestment1")
	public ResponseEntity<?> creditinvestment(@RequestParam(required = false) String cidNo) {
		try {
			if (cidNo == null) {
				return ResponseEntity.badRequest().body("Error: please enter cid number of a policy holder.");
			}
			JSONArray json = ricbDAO.getPolicyDetails(cidNo, "creditinvestment");
			return ResponseEntity.ok(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

//    @GetMapping("/creditinvestmentdetails1")
//    public ResponseEntity<?> creditinvestmentdetails(@RequestParam(required = false) String accountNo) {
//        try {
//            if (accountNo == null) {
//                return ResponseEntity.badRequest().body("Error: please enter acc number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getCreditDetails(accountNo);
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/getotipplan")
//    public ResponseEntity<?> getotipplan(
//            @RequestParam(required = false) String days,
//            @RequestParam(required = false) String age) {
//        try {
//            if (days == null) {
//                return ResponseEntity.badRequest().body("Error: please enter acc number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getotipplan(days, age);
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }
//
//    @GetMapping("/getotipplanjp")
//    public ResponseEntity<?> getotipplanjp(
//            @RequestParam(required = false) String days,
//            @RequestParam(required = false) String age) {
//        try {
//            if (days == null) {
//                return ResponseEntity.badRequest().body("Error: please enter acc number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getotipplanjp(days, age);
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/getotipplanasian")
//    public ResponseEntity<?> getotipplanasian(
//            @RequestParam(required = false) String days,
//            @RequestParam(required = false) String age) {
//        try {
//            if (days == null) {
//                return ResponseEntity.badRequest().body("Error: please enter acc number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getotipplanasian(days, age);
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/getotipplanall")
//    public ResponseEntity<?> getotipplanall(
//            @RequestParam(required = false) String days,
//            @RequestParam(required = false) String age) {
//        try {
//            if (days == null) {
//                return ResponseEntity.badRequest().body("Error: please enter acc number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getotipplanall(days, age);
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/getotipplanfinal")
//    public ResponseEntity<?> getotipplanfinal(
//            @RequestParam(required = false) String days,
//            @RequestParam(required = false) String age,
//            @RequestParam(required = false) String plan) {
//        try {
//            if (days == null) {
//                return ResponseEntity.badRequest().body("Error: please enter acc number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getotipplanfinal(days, age, plan);
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/genclaimStatus")
//    public ResponseEntity<?> genclaimStatus(@RequestParam(required = false) String accountNo) {
//        try {
//            if (accountNo == null) {
//                return ResponseEntity.badRequest().body("Error: please enter acc number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.genclaimStatus(accountNo);
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/geninsurance")
//    public ResponseEntity<?> genInsurance(@RequestParam(required = false) String cidNo) {
//        try {
//            if (cidNo == null) {
//                return ResponseEntity.badRequest().body("Error: please enter cidNo number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getPolicyDetails(cidNo, "generalinsurance");
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/motortppolicy")
//    public ResponseEntity<?> motortppolicy(@RequestParam(required = false) String cidNo) {
//        try {
//            if (cidNo == null) {
//                return ResponseEntity.badRequest().body("Error: please enter cidNo number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getPolicyDetails(cidNo, "motortppolicy");
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/motortpactivepolicy")
//    public ResponseEntity<?> motortpactivepolicy(@RequestParam(required = false) String cidNo) {
//        try {
//            if (cidNo == null) {
//                return ResponseEntity.badRequest().body("Error: please enter cidNo number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getPolicyDetails(cidNo, "motortpactivepolicy");
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

	@GetMapping("/otipactivepolicy")
	public ResponseEntity<?> otipactivepolicy(@RequestParam(required = false) String cidNo) {
		try {
			if (cidNo == null) {
				return ResponseEntity.badRequest().body("Error: please enter cidNo number of a policy holder.");
			}
			JSONArray json = ricbDAO.getPolicyDetails(cidNo, "otipactivepolicy");
			return ResponseEntity.ok(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

//    @GetMapping("/genclaimothers")
//    public ResponseEntity<?> genClaimOthers(@RequestParam(required = false) String cidNo) {
//        try {
//            if (cidNo == null) {
//                return ResponseEntity.badRequest().body("Error: please enter cidNo number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getPolicyDetails(cidNo, "genclaimothers");
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

	@GetMapping("/genTrackclaim")
	public ResponseEntity<?> genTrackclaim(@RequestParam(required = false) String cidNo) {
		try {
			if (cidNo == null) {
				return ResponseEntity.badRequest().body("Error: please enter cidNo number of a policy holder.");
			}
			JSONArray json = ricbDAO.getPolicyDetails(cidNo, "genTrackclaim");
			return ResponseEntity.ok(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

	@GetMapping("/getrurallife")
	public ResponseEntity<?> ruralLife(@RequestParam(required = false) String cidNo,
			@RequestParam(required = false) String uwYear) {
		try {
			if (cidNo == null) {
				return ResponseEntity.badRequest().body("Error: please enter cidNo number of a policy holder.");
			}
			JSONArray json = ricbDAO.getRLIdetails(cidNo, uwYear);
			return ResponseEntity.ok(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

//    @GetMapping("/getruralmember")
//    public ResponseEntity<?> getruralmember(
//            @RequestParam(required = false) String cidNo,
//            @RequestParam(required = false) String uwYear) {
//        try {
//            if (cidNo == null) {
//                return ResponseEntity.badRequest().body("Error: please enter cidNo number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getRLImember(cidNo, uwYear);
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/getOtipCustomer")
//    public ResponseEntity<?> getOtipCustomer(@RequestParam(required = false) String cidNo) {
//        try {
//            if (cidNo == null) {
//                return ResponseEntity.badRequest().body("Error: please enter cidNo number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getPolicyDetails(cidNo, "getOtipCustomer");
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }
//
//    @GetMapping("/checkrlpayment")
//    public ResponseEntity<?> checkrlpayment(@RequestParam(required = false) String serialNo) {
//        try {
//            if (serialNo == null) {
//                return ResponseEntity.badRequest().body("Error: please enter house number.");
//            }
//            JSONArray json = ricbDAO.checkrlpayment(serialNo);
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/geninsurancedetails")
//    public ResponseEntity<?> genInsuranceDetails(@RequestParam(required = false) String policyNo) {
//        try {
//            if (policyNo == null) {
//                return ResponseEntity.badRequest().body("Error: please enter policy number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getGeneralPolicyDetails(policyNo);
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/motorinsurancedetails")
//    public ResponseEntity<?> motorinsurancedetails(@RequestParam(required = false) String policyNo) {
//        try {
//            if (policyNo == null) {
//                return ResponseEntity.badRequest().body("Error: please enter policy number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.motorinsurancedetails(policyNo);
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/deferredannuity")
//    public ResponseEntity<?> deferredannuity(@RequestParam(required = false) String cidNo) {
//        try {
//            if (cidNo == null) {
//                return ResponseEntity.badRequest().body("Error: please enter cid number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getPolicyDetails(cidNo, "deferredannuity");
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/deferredannuitydetails")
//    public ResponseEntity<?> deferredannuitydetails(@RequestParam(required = false) String policyNo) {
//        try {
//            if (policyNo == null) {
//                return ResponseEntity.badRequest().body("Error: please enter policy number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getDeferredDetails(policyNo);
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/immediateannuity")
//    public ResponseEntity<?> immediateannuity(@RequestParam(required = false) String cidNo) {
//        try {
//            if (cidNo == null) {
//                return ResponseEntity.badRequest().body("Error: please enter cid number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getPolicyDetails(cidNo, "immediateannuity");
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/immediateannuitydetails")
//    public ResponseEntity<?> immediateannuitydetails(@RequestParam(required = false) String policyNo) {
//        try {
//            if (policyNo == null) {
//                return ResponseEntity.badRequest().body("Error: please enter policy number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getImmediateDetails(policyNo);
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

	@GetMapping("/insertLifePayment")
	public ResponseEntity<?> insertLifePayment(@RequestParam String cidNo, @RequestParam String custName,
			@RequestParam String deptCode, @RequestParam String policyNo, @RequestParam String amount,
			@RequestParam String orderNo, @RequestParam String remitterCid) {
		try {
			if (policyNo == null) {
				return ResponseEntity.badRequest().body("Error: please enter policy number of a policy holder.");
			}
			JSONArray json = ricbDAO.insertLifePaymentDetails(cidNo, custName, deptCode, policyNo, amount, orderNo,
					remitterCid);
			return ResponseEntity.ok(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

	@GetMapping("/updatePaymentFinal")
	public ResponseEntity<?> updatePaymentFinal(@RequestParam String txnId, @RequestParam String remitterBank,
			@RequestParam String accNo, @RequestParam String authCode, @RequestParam String orderNo) {
		try {
			if (txnId == null) {
				return ResponseEntity.badRequest().body("Error: please enter policy number of a policy holder.");
			}
			JSONArray json = ricbDAO.updateLifePaymentDetails(txnId, remitterBank, accNo, authCode, orderNo);
			return ResponseEntity.ok(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

	@GetMapping("/lineofbusiness")
	public ResponseEntity<?> lineofbusiness() {
		try {
			JSONArray json = ricbDAO.lineofbusiness();
			return ResponseEntity.ok(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

	@GetMapping("/lineofbusinessdetails")
	public ResponseEntity<?> lineofbusinessdetails(@RequestParam String id) {
		try {
			JSONArray json = ricbDAO.lineofbusinessdetails(id);
			return ResponseEntity.ok(json.toString().replaceAll("   ", ""));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

	@GetMapping("/advertisement")
	public ResponseEntity<?> advertisement() {
		try {
			JSONArray json = ricbDAO.advertisement();
			return ResponseEntity.ok(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

	@GetMapping("/updatePassword")
	public ResponseEntity<?> updatePassword(@RequestParam String cidNo, @RequestParam String mobileNo,
			@RequestParam String password) {
		try {
			JSONArray json = ricbDAO.updatePassword(cidNo, mobileNo, password);
			return ResponseEntity.ok(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

//    @GetMapping("/ppfmemo")
//    public ResponseEntity<?> ppfMemo(@RequestParam(required = false) String cidNo) {
//        try {
//            if (cidNo == null) {
//                return ResponseEntity.badRequest().body("Error: please enter cid number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getPolicyDetails(cidNo, "ppfmemo");
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }
//
//    @GetMapping("/gis")
//    public ResponseEntity<?> gis(@RequestParam(required = false) String cidNo) {
//        try {
//            if (cidNo == null) {
//                return ResponseEntity.badRequest().body("Error: please enter cid number of a policy holder.");
//            }
//            JSONArray json = ricbDAO.getPolicyDetails(cidNo, "gis");
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

	@GetMapping("/userprofile")
	public ResponseEntity<?> userprofile(@RequestParam(required = false) String cidNo) {
		try {
			if (cidNo == null) {
				return ResponseEntity.badRequest().body("Error: please enter cid number of a policy holder.");
			}
			JSONArray json = ricbDAO.getUserDetails(cidNo);
			return ResponseEntity.ok(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

	@GetMapping("/sendmail")
	public ResponseEntity<?> sendmail(@RequestParam String to, @RequestParam String from, @RequestParam String subject,
			@RequestParam String message) {
		try {
			if (to == null) {
				return ResponseEntity.badRequest().body("Error: please enter all mail details.");
			}
			JSONArray json = ricbDAO.sendmail(to, from, subject, message);
			return ResponseEntity.ok(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

	@GetMapping("/forgotpasswordotp")
	public ResponseEntity<?> forgotpasswordotp(@RequestParam String cidNumber, @RequestParam String otp) {
		try {
			if (cidNumber == null) {
				return ResponseEntity.badRequest().body("Error: please enter all mail details.");
			}
			JSONArray json = ricbDAO.forgotpasswordotp(cidNumber, otp);
			return ResponseEntity.ok(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

//    @GetMapping("/ppfmemostatement")
//    public ResponseEntity<?> ppfmemostatement(@RequestParam String cidNumber) {
//        try {
//            if (cidNumber == null) {
//                return ResponseEntity.badRequest().body("Error: please enter cid details.");
//            }
//            JSONArray json = ricbDAO.ppfmemo(cidNumber);
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

	@GetMapping("/paymentrequest")
	public ResponseEntity<?> paymentrequest(@RequestParam String messagetype, @RequestParam String amount,
			@RequestParam String email) {
		System.out.println("Msg Type: " + messagetype);
		System.out.println("Amount: " + amount);
		System.out.println("Email: " + email);
		String finalCheckSum;
		StringBuffer result = new StringBuffer();
		String resultRc = "";
		JSONArray json = new JSONArray();
		JSONObject jsonObject = new JSONObject();

		String bfs_msgType = messagetype;
		String bfs_txnAmount = amount;
		String bfs_remitterEmail = email;

		try {
			String bfs_benfTxnTime = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
			String bfs_orderNo = genOrderNo();
			String bfs_benfId = "BE10000071";
			String bfs_benfBankCode = "01";
			String bfs_txnCurrency = "BTN";
			String bfs_paymentDesc = "Insurance Fee";
			String bfs_version = "1.0";
			String bfs_checkSum = bfs_benfBankCode + "|" + bfs_benfId + "|" + bfs_benfTxnTime + "|" + bfs_msgType + "|"
					+ bfs_orderNo + "|" + bfs_paymentDesc + "|" + bfs_remitterEmail + "|" + bfs_txnAmount + "|"
					+ bfs_txnCurrency + "|" + bfs_version;
			finalCheckSum = BFSPKIImplementation.signData("/home/pki/BE10000071.key", bfs_checkSum, "SHA1withRSA");
			System.out.println("final check sum: " + finalCheckSum);

			HttpPost httppost = new HttpPost("https://bfssecure.rma.org.bt/BFSSecure/nvpapi?");
			httppost.setHeader("Content-type", "application/x-www-form-urlencoded; charset-utf-8");

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("bfs_msgType", bfs_msgType));
			params.add(new BasicNameValuePair("bfs_benfTxnTime", bfs_benfTxnTime));
			params.add(new BasicNameValuePair("bfs_orderNo", bfs_orderNo));
			params.add(new BasicNameValuePair("bfs_benfId", bfs_benfId));
			params.add(new BasicNameValuePair("bfs_benfBankCode", bfs_benfBankCode));
			params.add(new BasicNameValuePair("bfs_txnCurrency", bfs_txnCurrency));
			params.add(new BasicNameValuePair("bfs_txnAmount", bfs_txnAmount));
			params.add(new BasicNameValuePair("bfs_remitterEmail", bfs_remitterEmail));
			params.add(new BasicNameValuePair("bfs_paymentDesc", bfs_paymentDesc));
			params.add(new BasicNameValuePair("bfs_version", bfs_version));
			params.add(new BasicNameValuePair("bfs_checkSum", finalCheckSum));
			httppost.setEntity(new UrlEncodedFormEntity(params));

			DefaultHttpClient client = new DefaultHttpClient();
			CloseableHttpResponse httpresonse = client.execute(httppost);

			int statusCode = httpresonse.getStatusLine().getStatusCode();
			System.out.println("Status Code: " + statusCode);

			if (statusCode == 200) {
				System.out.println("Response: " + httpresonse.getEntity().getContent());

				BufferedReader rd = new BufferedReader(new InputStreamReader(httpresonse.getEntity().getContent()));
				String line = "";

				while ((line = rd.readLine()) != null) {
					result.append(line);
				}

				resultRc = URLDecoder.decode(result.toString(), "UTF-8");
				jsonObject.put("arresponse", resultRc);
				json.put(jsonObject);

				resultRc = json.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}

		return ResponseEntity.ok(resultRc);
	}

	@GetMapping("/accountenq")
	public ResponseEntity<?> returnVerificationResponse(@RequestParam String messagetype,
			@RequestParam String benf_TxnId, @RequestParam String bfs_remitterBankId,
			@RequestParam String bfs_remitterAccNo) {
		String returnString = null;
		String finalCheckSum;
		StringBuffer result = new StringBuffer();
		String resultRc = "";
		JSONArray json = new JSONArray();
		JSONObject jsonObject = new JSONObject();

		String bfs_msgType = messagetype;
		String bfs_benfTxnId = benf_TxnId;

		try {
			String bfs_benfId = "BE10000071";
			String bfs_checkSum = bfs_benfId + "|" + bfs_benfTxnId + "|" + bfs_msgType + "|" + bfs_remitterAccNo + "|"
					+ bfs_remitterBankId;
			System.out.println("checksum=" + bfs_checkSum);
			finalCheckSum = BFSPKIImplementation.signData("/home/pki/BE10000071.key", bfs_checkSum, "SHA1withRSA");

			HttpPost httppost = new HttpPost("https://bfssecure.rma.org.bt/BFSSecure/nvpapi?");
			httppost.setHeader("Content-type", "application/x-www-form-urlencoded; charset-utf-8");

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("bfs_msgType", bfs_msgType));
			params.add(new BasicNameValuePair("bfs_bfsTxnId", bfs_benfTxnId));
			params.add(new BasicNameValuePair("bfs_benfId", bfs_benfId));
			params.add(new BasicNameValuePair("bfs_remitterBankId", bfs_remitterBankId));
			params.add(new BasicNameValuePair("bfs_remitterAccNo", bfs_remitterAccNo));
			params.add(new BasicNameValuePair("bfs_checkSum", finalCheckSum));
			httppost.setEntity(new UrlEncodedFormEntity(params));

			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse httpresonse = (HttpResponse) client.execute(httppost);

			int statusCode = ((org.apache.http.HttpResponse) httpresonse).getStatusLine().getStatusCode();
			System.out.println("statusCode =========> " + statusCode);
			if (statusCode == 200) {
				BufferedReader rd = new BufferedReader(
						new InputStreamReader(((org.apache.http.HttpResponse) httpresonse).getEntity().getContent()));
				String line = "";

				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				System.out.println("result is ---->>>>>>>>>>>>" + result);

				resultRc = URLDecoder.decode(result.toString(), "UTF-8");
				jsonObject.put("aeresponse", resultRc);
				json.put(jsonObject);

				resultRc = json.toString();
			}
		} catch (Exception e) {
			returnString = "###Error at PaymentAction[openPayment]: " + e;
			return ResponseEntity.ok(returnString);
		}
		return ResponseEntity.ok(resultRc);
	}

	@GetMapping("/finalPaymentRequest")
	public ResponseEntity<?> returnPaymentResponse(@RequestParam String messagetype, @RequestParam String benf_TxnId,
			@RequestParam String bfs_remitterOtp) {
		System.out.println("Message type: " + messagetype);
		System.out.println("Transaction Id: " + benf_TxnId);
		System.out.println("OTP: " + bfs_remitterOtp);

		String returnString = null;
		String finalCheckSum;
		StringBuffer result = new StringBuffer();
		String resultRc = "";
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();

		try {
			String bfs_msgType = messagetype;
			String bfs_benfTxnId = benf_TxnId;

			String bfs_benfId = "BE10000071";
			String bfs_checkSum = bfs_benfId + "|" + bfs_benfTxnId + "|" + bfs_msgType + "|" + bfs_remitterOtp;
			System.out.println("checksum=" + bfs_checkSum);
			finalCheckSum = BFSPKIImplementation.signData("/home/pki/BE10000071.key", bfs_checkSum, "SHA1withRSA");

			HttpPost httppost = new HttpPost("https://bfssecure.rma.org.bt/BFSSecure/nvpapi?");
			httppost.setHeader("Content-type", "application/x-www-form-urlencoded; charset-utf-8");

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("bfs_msgType", bfs_msgType));
			params.add(new BasicNameValuePair("bfs_bfsTxnId", bfs_benfTxnId));
			params.add(new BasicNameValuePair("bfs_benfId", bfs_benfId));
			params.add(new BasicNameValuePair("bfs_remitterOtp", bfs_remitterOtp));
			params.add(new BasicNameValuePair("bfs_checkSum", finalCheckSum));
			httppost.setEntity(new UrlEncodedFormEntity(params));

			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse httpresonse = (HttpResponse) client.execute(httppost);

			int statusCode = ((org.apache.http.HttpResponse) httpresonse).getStatusLine().getStatusCode();
			System.out.println("Status Code: " + statusCode);

			if (statusCode == 200) {
				System.out
						.println("Response: " + ((org.apache.http.HttpResponse) httpresonse).getEntity().getContent());
				BufferedReader rd = new BufferedReader(
						new InputStreamReader(((org.apache.http.HttpResponse) httpresonse).getEntity().getContent()));
				String line = "";

				while ((line = rd.readLine()) != null) {
					result.append(line);
				}

				resultRc = URLDecoder.decode(result.toString(), "UTF-8");
				jsonArray.put(resultRc);
				resultRc = jsonArray.toString();
				System.out.println("UABT respomse=" + resultRc);
			}
		} catch (Exception e) {
			returnString = "###Error at PaymentAction[openPayment]: " + e;
			return ResponseEntity.ok(returnString);
		}
		return ResponseEntity.ok(resultRc);
	}

	@GetMapping("/appversion")
	public ResponseEntity<?> appversion() {
		try {
			JSONArray json = ricbDAO.appversion();
			return ResponseEntity.ok(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

	@GetMapping("/getcountry")
	public ResponseEntity<?> getcountry() {
		try {
			JSONArray json = ricbDAO.getcountry();
			return ResponseEntity.ok(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Server was not able to process your request");
		}
	}

//    @GetMapping("/gettitle")
//    public ResponseEntity<?> gettitle() {
//        try {
//            JSONArray json = ricbDAO.gettitle();
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/getMstatus")
//    public ResponseEntity<?> getMstatus() {
//        try {
//            JSONArray json = ricbDAO.getMstatus();
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/getContactType")
//    public ResponseEntity<?> getContactType() {
//        try {
//            JSONArray json = ricbDAO.getContactType();
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

//    @GetMapping("/getSeqId")
//    public ResponseEntity<?> getSeqId() {
//        try {
//            JSONArray json = ricbDAO.getSeqId();
//            return ResponseEntity.ok(json.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Server was not able to process your request");
//        }
//    }

	private static String genOrderNo() {
		String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		Random rand = new Random();
		int num = rand.nextInt(999999999);
		String finalStr = null;
		StringBuffer sbf = new StringBuffer();
		sbf.append(num);
		sbf.append(timeStamp);
		finalStr = sbf.toString();
		System.out.println(finalStr);
		return finalStr;
	}
	
	//rural life 2026
    @GetMapping("/getRLIprem")
    public ResponseEntity<?> getRLIprem() {
      try {
        JSONArray json = ricbDAO.getRLIprem();
        return ResponseEntity.ok(json.toString());
      } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body("Server was not able to process your request");
      }
    }
    
    @GetMapping("/getPaidStatus")
    public ResponseEntity<?> getPaidStatus(@RequestParam(required = false) String householdNumber) {
        try {
            if (householdNumber == null) {
                return ResponseEntity.badRequest().body("Error: please enter household number.");
            }
            JSONArray json = ricbDAO.getPaidStatus(householdNumber);
            return ResponseEntity.ok(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Server was not able to process your request");
        }
    }
    
    //MCP RENEWAL
    
    @GetMapping("/motorcppolicy")
    public ResponseEntity<?> motorcppolicy(@RequestParam(required = false) String cidNo) {
      try {
        if (cidNo == null) {
          return ResponseEntity.badRequest().body("Error: please enter cidNo number of a policy holder.");
        }
        JSONArray json = ricbDAO.getPolicyDetails(cidNo, "motorcppolicy");
        return ResponseEntity.ok(json.toString());
      } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body("Server was not able to process your request");
      }
    }
    
    @GetMapping("/motormcpinsurancedetails")
    public ResponseEntity<?> motormcpinsurancedetails(@RequestParam(required = false) String policyNo) {
      try {
        if (policyNo == null) {
          return ResponseEntity.badRequest().body("Error: please enter Policy number.");
        }
        JSONArray json = ricbDAO.getInstance().motormcpinsurancedetails(policyNo);
        return ResponseEntity.ok(json.toString());
      } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body("Server was not able to process your request");
      }
    }
    
    @GetMapping("/mcpPrevdetails")
    public ResponseEntity<?> mcpPrevdetails(@RequestParam(required = false) String policyNo) {
      try {
        if (policyNo == null) {
          return ResponseEntity.badRequest().body("Error: please enter Policy number.");
        }
        JSONArray json = ricbDAO.getInstance().mcpPrevdetails(policyNo);
        return ResponseEntity.ok(json.toString());
      } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body("Server was not able to process your request");
      }
    }
    
    //FIRE-SF RENEWAL
    @GetMapping("/firesfpolicy")
    public ResponseEntity<?> firesfpolicy(@RequestParam(required = false) String cidNo) {
      try {
        if (cidNo == null) {
          return ResponseEntity.badRequest().body("Error: please enter cidNo number of a policy holder.");
        }
        JSONArray json = ricbDAO.getInstance().getPolicyDetails(cidNo, "firesfpolicy");
        return ResponseEntity.ok(json.toString());
      } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body("Server was not able to process your request");
      }
    }
    
    @GetMapping("/fireSFPrevdetails")
    public ResponseEntity<?> fireSFPrevdetails(@RequestParam(required = false) String policyNo) {
      try {
        if (policyNo == null) {
          return ResponseEntity.badRequest().body("Error: please enter Policy number.");
        }
        JSONArray json = ricbDAO.getInstance().fireSFPrevdetails(policyNo);
        return ResponseEntity.ok(json.toString());
      } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body("Server was not able to process your request");
      }
    }
	
}