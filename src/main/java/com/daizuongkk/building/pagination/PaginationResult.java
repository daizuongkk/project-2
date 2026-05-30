package com.daizuongkk.building.pagination;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Setter
@Getter
public class PaginationResult<E> {

	private final int totalRecords;
	private final int currentPage;
	private final List<E> list;
	private final int maxResult;
	private final int totalPages;
	private final int maxNavigationPage;
	private List<Integer> navigationPages;

	public PaginationResult(TypedQuery<E> query, TypedQuery<Long> countQuery, int page, int maxResult,
			int maxNavigationPage) {

		this.maxResult = maxResult;
		this.currentPage = Math.max(page, 1);

		// 1. Đếm số bản ghi
		this.totalRecords = countQuery.getSingleResult().intValue();

		// 2. Tính tổng số trang
		this.totalPages = (int) Math.ceil((double) totalRecords / maxResult);

		// 3. Lấy dữ liệu phân trang
		this.list = query.setFirstResult((currentPage - 1) * maxResult).setMaxResults(maxResult).getResultList();

		// 4. Tính navigation
		this.maxNavigationPage = Math.min(maxNavigationPage, totalPages);
		calcNavigationPages();
	}

	public PaginationResult(Query query, int countQuery, int page, int maxResult,
			int maxNavigationPage) {

		this.maxResult = maxResult;
		this.currentPage = Math.max(page, 1);

		// 1. Đếm số bản ghi
		this.totalRecords = countQuery;

		// 2. Tính tổng số trang
		this.totalPages = (int) Math.ceil((double) totalRecords / maxResult);

		// 3. Lấy dữ liệu phân trang
		this.list = query.setFirstResult((currentPage - 1) * maxResult).setMaxResults(maxResult).getResultList();

		// 4. Tính navigation
		this.maxNavigationPage = Math.min(maxNavigationPage, totalPages);
		calcNavigationPages();
	}

	private void calcNavigationPages() {
		navigationPages = new ArrayList<>();

		int current = Math.min(currentPage, totalPages);

		int begin = current - maxNavigationPage / 2;
		int end = current + maxNavigationPage / 2;

		navigationPages.add(1);

		if (begin > 2)
			navigationPages.add(-1);

		for (int i = begin; i <= end; i++) {
			if (i > 1 && i < totalPages) {
				navigationPages.add(i);
			}
		}

		if (end < totalPages - 2)
			navigationPages.add(-1);

		if (totalPages > 1)
			navigationPages.add(totalPages);
	}

}
