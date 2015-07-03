package com.aplos.cms.beans;

import java.util.ArrayList;
import java.util.List;

import com.aplos.cms.module.CmsConfiguration;

public class PageScroller {
	private Integer itemsPerPage = null; // uses ecommerce setting by default
	private int pagesToDisplaySimultaneously = 5;
	private int currentPage = 1;
	private int highestPageNumberToDisplay = 1;
	private boolean showFirstAndLastControls = true;
	private boolean showPreviousAndNextControls = false;
	private String firstLabel = "First";
	private String lastLabel = "Last";
	private String previousLabel = "<< Previous";
	private String nextLabel = "Next >>";

	public PageScroller() { }
	public PageScroller(int currentPage) { this.setCurrentPage(currentPage); }

	public void setItemsPerPage(int itemsPerPage) {
		this.itemsPerPage = itemsPerPage;
	}

	public void setPagesToList(int pagesToList) {
		this.pagesToDisplaySimultaneously = pagesToList;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getPagesToList() {
		return pagesToDisplaySimultaneously;
	}

	public int getItemsPerPage() {
		if (itemsPerPage == null) {
			itemsPerPage = CmsConfiguration.getCmsConfiguration().getPaginationItemsPerPage();
		}
		return itemsPerPage;
	}

	/*
	 * This takes the number of products returned by the query
	 * and works out how many pages will be needed
	 */
	public void setLastPage(int numberOfProducts) {
		int calculated = (int) Math.ceil( numberOfProducts / (double) getItemsPerPage() );
		this.highestPageNumberToDisplay = calculated;
	}

	public int getFirstProductIndex() {
		int fromInclusive = 0;
		if( currentPage != 0 ) {
			fromInclusive = (currentPage-1) * getItemsPerPage();
		}
		return fromInclusive;
	}

	public List<PaginationControl> getControls() {
		List<PaginationControl> controls = new ArrayList<PaginationControl>();

		if (highestPageNumberToDisplay>1) {
			if( showFirstAndLastControls ) {
				PaginationControl paginationControl = new PaginationControl(getFirstLabel(),1,currentPage == 1);
				paginationControl.setStyleClass( "aplos-pagination-first");
				controls.add(paginationControl);
			}
			if( showPreviousAndNextControls ) {
				PaginationControl paginationControl = new PaginationControl(getPreviousLabel(),currentPage-1,currentPage == 1);
				paginationControl.setStyleClass( "aplos-pagination-previous");
				controls.add(paginationControl);
			}
			
			controls.add(new PaginationControl("Pages: ",1,true));

			int thisPageNumber = currentPage - (int)Math.floor(pagesToDisplaySimultaneously/2);

			if ((thisPageNumber >= (int)Math.floor(pagesToDisplaySimultaneously/2) && highestPageNumberToDisplay > pagesToDisplaySimultaneously) || thisPageNumber > pagesToDisplaySimultaneously) {
				controls.add(new PaginationControl(" ...",currentPage-pagesToDisplaySimultaneously,false));
			}

			if (currentPage > (highestPageNumberToDisplay - ((int)Math.floor(pagesToDisplaySimultaneously/2)))) {
				if (highestPageNumberToDisplay == currentPage) {
					thisPageNumber = thisPageNumber - 2;
				} else {
					thisPageNumber = thisPageNumber - (highestPageNumberToDisplay - currentPage);
				}
			}

			if (thisPageNumber < 1) {
				thisPageNumber=1;
			}

			while (thisPageNumber < currentPage) {
				controls.add(new PaginationControl(" " + thisPageNumber,thisPageNumber,false));
				thisPageNumber++;
			}

			controls.add(new PaginationControl(" [" + currentPage + "]",currentPage,true));
			thisPageNumber = currentPage + 1;

			int cutoff;
			if (currentPage < pagesToDisplaySimultaneously-1) {
				cutoff = currentPage + (pagesToDisplaySimultaneously-currentPage);
			} else {
				cutoff = currentPage + (int)Math.floor(pagesToDisplaySimultaneously/2);
			}

			while (thisPageNumber <= highestPageNumberToDisplay && thisPageNumber <= cutoff) {
				controls.add(new PaginationControl(" " + thisPageNumber,thisPageNumber,false));
				thisPageNumber++;
			}

			thisPageNumber--;

			if (currentPage < highestPageNumberToDisplay - (int)Math.floor(pagesToDisplaySimultaneously/2) && thisPageNumber != highestPageNumberToDisplay) {
				controls.add(new PaginationControl(" ...",currentPage+pagesToDisplaySimultaneously,false));
			}

			if( showPreviousAndNextControls ) {
				PaginationControl paginationControl = new PaginationControl(getNextLabel(),currentPage + 1,currentPage == highestPageNumberToDisplay);
				paginationControl.setStyleClass( "aplos-pagination-next");
				controls.add(paginationControl);
			}
			if( showFirstAndLastControls ) {
				PaginationControl paginationControl = new PaginationControl( getLastLabel(),highestPageNumberToDisplay,currentPage == highestPageNumberToDisplay);
				paginationControl.setStyleClass( "aplos-pagination-last");
				controls.add(paginationControl);
			}
		}
		return controls;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = (currentPage < 1)? 1 : (currentPage > highestPageNumberToDisplay)? highestPageNumberToDisplay : currentPage;
	}

	public int getLastPage() {
		return highestPageNumberToDisplay;
	}

	public void nextPage() {
		currentPage++;
	}

	public void previousPage() {
		if (currentPage != 1) {
			currentPage--;
		}
	}

	//these two represent the ellipsis' functions

	public void scrollForward() {
		currentPage = currentPage + pagesToDisplaySimultaneously;
	}

	public void scrollBackward() {
		currentPage = currentPage - pagesToDisplaySimultaneously;
		if (currentPage < 1) {
			currentPage = 1;
		}
	}

	public boolean isShowFirstAndLastControls() {
		return showFirstAndLastControls;
	}
	public void setShowFirstAndLastControls(boolean showFirstAndLastControls) {
		this.showFirstAndLastControls = showFirstAndLastControls;
	}

	public boolean isShowPreviousAndNextControls() {
		return showPreviousAndNextControls;
	}
	public void setShowPreviousAndNextControls(boolean showPreviousAndNextControls) {
		this.showPreviousAndNextControls = showPreviousAndNextControls;
	}

	public String getFirstLabel() {
		return firstLabel;
	}
	public void setFirstLabel(String firstLabel) {
		this.firstLabel = firstLabel;
	}

	public String getLastLabel() {
		return lastLabel;
	}
	public void setLastLabel(String lastLabel) {
		this.lastLabel = lastLabel;
	}

	public String getPreviousLabel() {
		return previousLabel;
	}
	public void setPreviousLabel(String previousLabel) {
		this.previousLabel = previousLabel;
	}

	public String getNextLabel() {
		return nextLabel;
	}
	public void setNextLabel(String nextLabel) {
		this.nextLabel = nextLabel;
	}

	public class PaginationControl {

		private boolean isDisabled;
		private int pageNumber;
		private String displayText;
		private String styleClass = "";

		public PaginationControl (String displayText, int pageNumber, boolean isDisabled) {
			this.displayText = displayText;
			this.pageNumber = pageNumber;
			this.isDisabled = isDisabled;
		}

		public String getDisplayText() {
			return displayText;
		}

		public boolean getIsDisabled() {
			return isDisabled;
		}

		public int getPageNumber() {
			return pageNumber;
		}

		public void setDisplayText(String displayText) {
			this.displayText = displayText;
		}

		public void setIsDisabled(boolean isDisabled) {
			this.isDisabled = isDisabled;
		}

		public void setPageNumber (int pageNumber) {
			this.pageNumber = pageNumber;
		}

		public String getStyleClass() {
			return styleClass;
		}

		public void setStyleClass(String styleClass) {
			this.styleClass = styleClass;
		}
	}

}
