package cn.edu.hfut.dmic.dm.example.domain;

import java.util.ArrayList;
import java.util.List;

public class imginfo {
	private String id;//
	private String cid;//
	private String title;//
	private String tag;// '标签',
	private String color;// DEFAULT '',
	private String cover;// '封面',
	private String author;// '作者',
	private String comeurl;// '来源',
	private String remark;//
	private String short_title;//
	private String keywords;//
	private String content;//
	private String hits;// DEFAULT '0',
	private String star;// DEFAULT '1',
	private String status;// DEFAULT '1',
	private String up = "0";// DEFAULT '0',
	private String down = "0";// DEFAULT '0',
	private String addtime;// DEFAULT '0',

	private List<imgattach> imgattachlist;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getComeurl() {
		return comeurl;
	}

	public void setComeurl(String comeurl) {
		this.comeurl = comeurl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getShort_title() {
		return short_title;
	}

	public void setShort_title(String short_title) {
		this.short_title = short_title;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getHits() {
		return hits;
	}

	public void setHits(String hits) {
		this.hits = hits;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUp() {
		return up;
	}

	public void setUp(String up) {
		this.up = up;
	}

	public String getDown() {
		return down;
	}

	public void setDown(String down) {
		this.down = down;
	}

	public String getAddtime() {
		return addtime;
	}

	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}

	public List<imgattach> getImgattachlist() {
		if(imgattachlist==null){
			return new ArrayList<imgattach>();
		}
		return imgattachlist;
	}

	@Override
	public String toString() {
		return "INSERT INTO `tutu_article` VALUES ('"+id+"', '"+cid+"', '"+title+"', '"+tag+"', '', '"+cover+"', 'admin', '', '', NULL, NULL, '', 0, 1, 1, 0, 0, 0);"			;
	}

	
	

}

