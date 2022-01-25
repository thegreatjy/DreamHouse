package org.techtown.project_savedreamhouse;

public class Post {
    String title;
    String writer;
    String contents;
    String category;
    int id;

    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }

    public String getWriter(){
        return writer;
    }
    public void setWriter(String writer){
        this.writer = writer;
    }

    public String getContents(){
        return contents;
    }
    public void setContents(String contents){
        this.contents = contents;
    }

    public String getCategory(){
        return category;
    }
    public void setCategory(String category){
        this.category = category;
    }

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public Post(int id, String title, String writer, String contents, String category){
        this.title = title;
        this.writer = writer;
        this.contents = contents;
        this.category = category;
    }
    public Post(int id){
        this.id = id;
    }
}
