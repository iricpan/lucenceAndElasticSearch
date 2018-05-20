package cn.bd.elasticSearch.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

//指定索引的名称和文档类型
@Document(indexName = "blog3",type = "article")
public class Article {

    @Id//索引库中的唯一标识,字符串类型
    @Field(type = FieldType.Integer,store = true,index = FieldIndex.not_analyzed)
	private Integer id;
    @Field(type = FieldType.String,store = true,index = FieldIndex.analyzed,analyzer = "ik",searchAnalyzer = "ik")
	private String title;
    @Field(type = FieldType.String,store = true,index = FieldIndex.analyzed,analyzer = "ik",searchAnalyzer = "ik")
	private String content;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "Article [id=" + id + ", title=" + title + ", content=" + content + "]";
	}
	
	
}