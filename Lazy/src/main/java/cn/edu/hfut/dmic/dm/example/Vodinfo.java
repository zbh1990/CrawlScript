package cn.edu.hfut.dmic.dm.example;

public class Vodinfo {

	private String area;
	// 电影电视国产剧
	private String bigtype;
	// 动作，爱情，科幻
	private String smalltype;
	private String acteres;
	private String url;
	//视频title图片
	private String img;
	//幻灯图片
	private String imglide="";
	private String title;
	private String desc;
	private String year;
	private String director;
	private String score;
	private String player;
	private int hits;
	private String needpay="";

	@Override
	public String toString() {
		return "INSERT INTO `mac_vod` ( `d_name`, `d_subname`, `d_enname`, `d_letter`, `d_color`, `d_pic`, `d_picthumb`, `d_picslide`, `d_starring`, `d_directed`, `d_tag`, `d_remarks`, `d_area`, `d_lang`, `d_year`, `d_type`, `d_type_expand`, `d_class`, `d_topic`, `d_hide`, `d_lock`, `d_state`, `d_level`, `d_usergroup`, `d_stint`, `d_stintdown`, `d_hits`, `d_dayhits`, `d_weekhits`, `d_monthhits`, `d_duration`, `d_up`, `d_down`, `d_score`, `d_scoreall`, `d_scorenum`, `d_addtime`, `d_time`, `d_hitstime`, `d_maketime`, `d_content`, `d_playfrom`, `d_playserver`, `d_playnote`, `d_playurl`, `d_downfrom`, `d_downserver`, `d_downnote`, `d_downurl`) VALUES ('"
				+ title + "', '', 'niuyueheibang', 'N', '', '" + img + "', '"+imglide+"', '"+imglide+"', '" + acteres + "', '" + director
				+ "', '" + smalltype + "', '"+needpay+"', '" + area + "', '', " + year + ", " + bigtype
				+ ", '', '', '0', 0, 0, 0, 0, 0, 0, 0, " + hits + ", 0, 5, 209, 0, 0, 0, 7.0, 2394, 342, "
				+ System.currentTimeMillis() / 1000 + ", " + System.currentTimeMillis() / 1000 + ", "
				+ System.currentTimeMillis() / 1000 + ", 0, '" + desc + "', '" + player + "', '0', '', '" + url
				+ "', '', '', '', '')ON DUPLICATE KEY UPDATE   d_playfrom='" + player + "',  d_time="
				+ System.currentTimeMillis() / 1000 + ", d_playurl='"+url+"';";
	}

	
	

	public String getNeedpay() {
		return needpay;
	}




	public void setNeedpay(String needpay) {
		this.needpay = needpay;
	}




	public String getImglide() {
		return imglide;
	}


	public void setImglide(String imglide) {
		this.imglide = imglide;
	}


	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getBigtype() {
		return bigtype;
	}

	public void setBigtype(String bigtype) {
		this.bigtype = bigtype;
	}

	public String getSmalltype() {
		return smalltype;
	}

	public void setSmalltype(String smalltype) {
		this.smalltype = smalltype;
	}

	public String getActeres() {
		return acteres;
	}

	public void setActeres(String acteres) {
		this.acteres = acteres;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

}
