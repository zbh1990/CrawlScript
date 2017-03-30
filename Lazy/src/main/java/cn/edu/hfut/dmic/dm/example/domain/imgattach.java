package cn.edu.hfut.dmic.dm.example.domain;

public class imgattach {
	private String article_id;// '主题ID',
	private String uid = "1";// '用户ID',
	private String name;// '文件名',
	private String remark;// '文件描述',
	private String size;// '文件大小',
	private String file;// '文件路径',
	private String ext;// '文件类型',
	private String status = "1";// '状态, 1:正常 0:隐藏',
	private String type = "0";// '附件类型, 0:本地文件, 1:网络文件',
	private String try_count = "0";// '重试次数',

	public String getArticle_id() {
		return article_id;
	}

	public void setArticle_id(String article_id) {
		this.article_id = article_id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTry_count() {
		return try_count;
	}

	public void setTry_count(String try_count) {
		this.try_count = try_count;
	}

	@Override
	public String toString() {
		return "INSERT INTO `tutu_attach` (`article_id`,`uid` ,`remark`, `size`,`file`,`ext`,`status`,`type`,`try_count`) VALUES ( '"+article_id+"', 1, '', 0, '"+file+"', 'jpg', 1, 0, 0);";
	}
	

}
