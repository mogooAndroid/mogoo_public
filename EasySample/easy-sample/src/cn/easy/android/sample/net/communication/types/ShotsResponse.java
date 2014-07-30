package cn.easy.android.sample.net.communication.types;

import java.util.List;

public class ShotsResponse {

	private int page;
	private int pages;
	private int per_page;
	private int total;
	private List<Shot> shots;
	
	public int getPage() {
		return page;
	}

	public ShotsResponse setPage(int page) {
		this.page = page;
		return this;
	}

	public int getPages() {
		return pages;
	}

	public ShotsResponse setPages(int pages) {
		this.pages = pages;
		return this;
	}

	public int getPer_page() {
		return per_page;
	}

	public ShotsResponse setPerpage(int perPage) {
		this.per_page = perPage;
		return this;
	}

	public int getTotal() {
		return total;
	}

	public ShotsResponse setTotal(int total) {
		this.total = total;
		return this;
	}
	
	public List<Shot> getShots() {
		return shots;
	}

	public ShotsResponse setShots(List<Shot> shots) {
		this.shots = shots;
		return this;
	}

	@Override
	public String toString() {
		return "ShotsResponse [page=" + page + ", pages=" + pages
				+ ", per_page=" + per_page + ", total=" + total + ", shots="
				+ shots + "]";
	}
}
