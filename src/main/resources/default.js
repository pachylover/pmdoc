document.addEventListener("DOMContentLoaded", function () {
	const collapsibles = document.querySelectorAll(".collapsible");
	
	collapsibles.forEach((item) => {
		item.addEventListener("click", function () {
			this.classList.toggle("active");
			
			// 현재 클릭된 요소의 하위 collapsible-content 찾기
			const content = this.nextElementSibling;
			
			if (content.classList.contains("expanded")) {
				// 접기
				content.style.maxHeight = "0";
				content.classList.remove("expanded");
			} else {
				// 펼치기
				content.style.maxHeight = getFullHeight(content) + "px";
				content.classList.add("expanded");
			}
		});
	});
	
	/**
	 * 요소의 전체 높이를 재귀적으로 계산
	 * @param {HTMLElement} element
	 * @returns {number}
	 */
	function getFullHeight(element) {
		let height = element.scrollHeight; // 현재 요소의 높이
		const children = element.children;
		
		// 자식 요소 중 collapsible-content가 있다면 그 높이도 추가
		for (let child of children) {
			// child의 자식 요소 중 collapsible-content가 있다면
			child.querySelectorAll(".collapsible-content").forEach((item) => {
				item.classList.add("expanded");
				height += getFullHeight(item);
				item.classList.remove("expanded");
			});
		}
		
		return height + 30;
	}
});