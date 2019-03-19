package com.zm.Bean;


import java.util.List;

public class UserBean {
    private String user;
    private int user_id;
    private String password;
    private String name;
    private String company_id;
    private String company_name;
    private String company_address;
    private double company_latitude;
    private double company_longtitude;

    private String department_id;
    private String department_name;
    private String department_address;
    private double department_latitude;
    private double department_longtitude;


    private Double sign_latitude;
    private Double sign_longtitude;
    private String sign_addrees;
    private boolean checked;
    private boolean registered;
    private boolean signed;
    private boolean address_signed;
    private boolean img_signed;
	private String err_str;
	private String warn_str;

	private ImgBean img;
	private ImgBean reviewImg;
    
    private int img_type;
    private boolean final_check=false;
    private List<SignItemBean>  signList;
    private int review_status;
    
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Double getSign_latitude() {
        return sign_latitude;
    }
    public void setSign_latitude(Double sign_latitude) {
        this.sign_latitude = sign_latitude;
    }
    public Double getSign_longtitude() {
        return sign_longtitude;
    }
    public void setSign_longtitude(Double sign_longtitude) {
        this.sign_longtitude = sign_longtitude;
    }
    public String getSign_addrees() {
        return sign_addrees;
    }
    public void setSign_addrees(String sign_addrees) {
        this.sign_addrees = sign_addrees;
    }
    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    public String getErr_str() {
        return err_str;
    }
    public void setErr_str(String err_str) {
        this.err_str = err_str;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }
	public ImgBean getImg() {
		return img;
	}
	public void setImg(ImgBean img) {
		this.img = img;
	}
	public int getImg_type() {
		return img_type;
	}
	public void setImg_type(int img_type) {
		this.img_type = img_type;
	}
	public boolean isSigned() {
		return signed;
	}
	public void setSigned(boolean signed) {
		this.signed = signed;
	}
	public String getWarn_str() {
		return warn_str;
	}
	public void setWarn_str(String warn_str) {
		this.warn_str = warn_str;
	}
	public boolean isAddress_signed() {
		return address_signed;
	}
	public void setAddress_signed(boolean address_signed) {
		this.address_signed = address_signed;
	}
	public boolean isImg_signed() {
		return img_signed;
	}
	public void setImg_signed(boolean img_signed) {
		this.img_signed = img_signed;
	}
	public boolean isFinal_check() {
		return final_check;
	}
	public void setFinal_check(boolean final_check) {
		this.final_check = final_check;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

    public List<SignItemBean> getSignList() {
        return signList;
    }

    public void setSignList(List<SignItemBean> signList) {
        this.signList = signList;
    }
	public String getCompany_id() {
		return company_id;
	}
	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getCompany_address() {
		return company_address;
	}
	public void setCompany_address(String company_address) {
		this.company_address = company_address;
	}
	public double getCompany_latitude() {
		return company_latitude;
	}
	public void setCompany_latitude(double company_latitude) {
		this.company_latitude = company_latitude;
	}
	public double getCompany_longtitude() {
		return company_longtitude;
	}
	public void setCompany_longtitude(double company_longtitude) {
		this.company_longtitude = company_longtitude;
	}
	public String getDepartment_id() {
		return department_id;
	}
	public void setDepartment_id(String department_id) {
		this.department_id = department_id;
	}
	public String getDepartment_name() {
		return department_name;
	}
	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}
	public String getDepartment_address() {
		return department_address;
	}
	public void setDepartment_address(String department_address) {
		this.department_address = department_address;
	}
	public double getDepartment_latitude() {
		return department_latitude;
	}
	public void setDepartment_latitude(double department_latitude) {
		this.department_latitude = department_latitude;
	}
	public double getDepartment_longtitude() {
		return department_longtitude;
	}
	public void setDepartment_longtitude(double department_longtitude) {
		this.department_longtitude = department_longtitude;
	}

	public ImgBean getReviewImg() {
		return reviewImg;
	}

	public void setReviewImg(ImgBean reviewImg) {
		this.reviewImg = reviewImg;
	}

	public int getReview_status() {
		return review_status;
	}

	public void setReview_status(int review_status) {
		this.review_status = review_status;
	}
}
