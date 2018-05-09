package cn.itheima.po;

/**
 * 图书pojo
 * @author Administrator
 *
 */
public class Book {

	
	private Integer id;
	private String bookName;
	private Float price;
	private String pic;
	private String bookdesc;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getBookdesc() {
		return bookdesc;
	}

	public void setBookdesc(String bookdesc) {
		this.bookdesc = bookdesc;
	}

	@Override
	public String toString() {
		return "Book [id=" + id + ", bookNmae=" + bookName + ", price=" + price + ", pic=" + pic + ", bookdesc="
				+ bookdesc + "]";
	}
	
}
