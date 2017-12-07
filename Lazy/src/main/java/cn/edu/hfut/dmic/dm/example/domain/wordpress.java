package cn.edu.hfut.dmic.dm.example.domain;

public class wordpress {

	private String content;
	private String title;
	private String id;
	private String cid;

	public String toString1() {
		return "INSERT INTO wp_posts(id,`post_author` ,  `post_date` ,  `post_date_gmt` ,  `post_content`,  `post_title`,  `post_excerpt`,  `post_status`,  `comment_status`,`ping_status`,  `post_password` ,  `post_name`,  `to_ping`,  `pinged`,  `post_modified` ,  `post_modified_gmt` ,  `post_content_filtered`, "
				+ " `post_parent` ,  `guid`,  `menu_order`,  `post_type`,  `post_mime_type` ,`comment_count` ) "
				+ "VALUES ("+"'"+ id + "',  '1', now(), now(), "
				+  "'"+ content + "', '" + title + "', '', 'publish', 'open', 'open', '', '" + title
				+ "', '', '', now(), now(), '', '0', '', '0', 'post', '', '0')" + "ON DUPLICATE KEY UPDATE   post_content='"
				+ content + "';";
	}
	public String toString2() {
		return " insert into wp_term_relationships(object_id,term_taxonomy_id,term_order) VALUES('"+id+"','"+cid+"',"+"'0');";
	}

	
	public String getCid() {
		return cid;
	}


	public void setCid(String cid) {
		this.cid = cid;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
