package com.ibm.portal.samples.mail.list.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.UIDFolder;
import javax.portlet.BaseURL;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.ResourceURL;
import javax.portlet.StateAwareResponse;

import com.ibm.portal.Disposable;
import com.ibm.portal.samples.common.AbstractModel;
import com.ibm.portal.samples.mail.common.MessageBean;
import com.ibm.portal.samples.mail.helper.AbstractJstlMap;
import com.ibm.portal.samples.mail.list.MailListBean;

/**
 * Implementation of the state handling aspects of the composer portlet. In this
 * portlet we maintain a paging view to the mail, so we have to encode the
 * current page size as well as the currently selected page. You can also select
 * one of the mails for preview.
 * 
 * @author cleue
 * 
 */
public class MailListModel extends AbstractModel implements Disposable,
		Cloneable {

	public interface Dependencies extends AbstractModel.Dependencies {

		/**
		 * TODO add dependencies via parameterless getter methods
		 */

	}

	/**
	 * @author cleue
	 * 
	 */
	private enum REFRESH_MODE {
		MAIL_CHECK, REFRESH;

		/**
		 * cache to prevent making a copy all the time
		 */
		private static REFRESH_MODE[] VALUES = values();
	}

	/**
	 * enumeration of the sort orders
	 */
	public enum SORT_ORDER {
		ASCENDING {
			@Override
			public SORT_ORDER getNext() {
				return DESCENDING;
			}

			@Override
			public SORT_ORDER getPrevious() {
				return NONE;
			}

			@Override
			public boolean isAscending() {
				return true;
			}

			@Override
			public boolean isDescending() {
				return false;
			}
		},

		DESCENDING {
			@Override
			public SORT_ORDER getNext() {
				return NONE;
			}

			@Override
			public SORT_ORDER getPrevious() {
				return ASCENDING;
			}

			@Override
			public boolean isAscending() {
				return false;
			}

			@Override
			public boolean isDescending() {
				return true;
			}
		},
		NONE {
			@Override
			public SORT_ORDER getNext() {
				return ASCENDING;
			}

			@Override
			public SORT_ORDER getPrevious() {
				return DESCENDING;
			}

			@Override
			public boolean isAscending() {
				return false;
			}

			@Override
			public boolean isDescending() {
				return false;
			}
		};

		/**
		 * Returns the next order
		 * 
		 * @return the next order
		 */
		public abstract SORT_ORDER getNext();

		/**
		 * Returns the previous
		 * 
		 * @return the previous order
		 */
		public abstract SORT_ORDER getPrevious();

		/**
		 * @return <code>true</code> if this is ascending sort, else
		 *         <code>false</code>
		 */
		public abstract boolean isAscending();

		/**
		 * @return <code>true</code> if this is descending sort, else
		 *         <code>false</code>
		 */
		public abstract boolean isDescending();
	}

	/**
	 * enumeration of the states we need to encode our view
	 */
	private enum STATE {
		/** currently selected page */
		CURRENT_PAGE,
		/** size of the page */
		PAGE_SIZE,
		/** refresh mode */
		REFRESH_MODE,
		/** sort column */
		SORT_COLUMN,
		/** sort order */
		SORT_ORDER
	}

	/**
	 * default page selection
	 */
	private static final int DEFAULT_PAGE = 0;

	/**
	 * default page size
	 */
	private static final int DEFAULT_PAGE_SIZE = 10;

	/**
	 * default mode for refreshing
	 */
	private static final REFRESH_MODE DEFAULT_REFRESH = null;

	/**
	 * default selection
	 */
	private static final long DEFAULT_SELECTION_ID = Long.MIN_VALUE;

	/**
	 * default column to sort
	 */
	private static final int DEFAULT_SORT_COLUMN = 0;

	/**
	 * default sort
	 */
	private static final SORT_ORDER DEFAULT_SORT_ORDER = SORT_ORDER.NONE;

	/**
	 * local name of the public render parameter that identifies the selection
	 */
	private static final String KEY_SELECTED = "sel";

	/** class name for the logger */
	private static final String LOG_CLASS = MailListModel.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * minimum page size
	 */
	private static final int MINIMUM_PAGE_SIZE = 3;

	/**
	 * quick pages constants if no page exists
	 */
	private static final long[] NO_PAGES = new long[0];

	/**
	 * quick pages constants if one page exists
	 */
	private static final long[] ONE_PAGE = new long[] { 0 };

	/**
	 * list of applicable page sizes
	 */
	private static final int[] PAGE_SIZES = new int[] { 5, 10, 20, 50, 100 };

	/**
	 * number of pages in the quick jump area
	 */
	private static final long QUICK_PAGE_COUNT = 5;

	/**
	 * array of possible sort orders
	 */
	private static final SORT_ORDER[] SORT_ORDER_VALUES = SORT_ORDER.values();

	/**
	 * quick pages constants if two pages exist
	 */
	private static final long[] TWO_PAGES = new long[] { 0, 1 };

	/**
	 * indicator for an uninitialized integer
	 */
	private static final int UNDEFINED_INT = Integer.MIN_VALUE;

	/**
	 * indicator for an uninitialized long
	 */
	private static final long UNDEFINED_LONG = Long.MIN_VALUE;

	/**
	 * checks if the application is authenticated
	 */
	private Boolean bAuthenticated;

	/**
	 * access to the services
	 */
	private final MailListBean bean;

	/**
	 * logging support, we can do this as a instance variable since the model
	 * bean is instantiated for every request
	 */
	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	/**
	 * checks if the application is logged in
	 */
	private Boolean bLoggedIn;

	/**
	 * current page
	 */
	private int curPage;

	/**
	 * currently selected page
	 */
	private int currentPage = -1;

	/**
	 * ID of the item that was selected when the model was built up
	 */
	private final long currentSelectedId;

	/**
	 * index of the first item
	 */
	private int firstItem = -1;

	private Folder folder;

	/**
	 * total number of items
	 */
	private int itemCount = -1;

	/**
	 * current list of items for the selected page
	 */
	private List<MessageBean> items;

	/**
	 * index after the last item
	 */
	private int lastItem = -1;

	/**
	 * returns the most recent message
	 */
	private MessageBean latestMessage;

	/**
	 * total number of pages
	 */
	private int pageCount = -1;

	/**
	 * direct links to pages
	 */
	private long[] pages;

	/**
	 * current page size
	 */
	private int pageSize;

	/**
	 * the refresh mode
	 */
	private REFRESH_MODE refreshMode;

	/**
	 * ID of the currently selected item
	 */
	private long selectedId;

	/**
	 * date access
	 */
	private Map<Object, Object> selectedMap;

	private int sortColumn;

	/**
	 * current sort order
	 */
	private SORT_ORDER sortOrder;

	/**
	 * Decode the model from the request
	 * 
	 * @param aBean
	 *            access to services
	 * @param aReq
	 *            the request
	 */
	public MailListModel(final MailListBean aBean, final PortletRequest aReq,
			final Dependencies aDeps) {
		// default handling
		super(aReq, aDeps);
		// logging support
		final String LOG_METHOD = "MailListModel(aBean, aReq, aDeps)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aBean, aReq });
		}
		// decode
		bean = aBean;

		curPage = decode(STATE.CURRENT_PAGE, DEFAULT_PAGE);
		pageSize = decode(STATE.PAGE_SIZE, DEFAULT_PAGE_SIZE);
		sortColumn = 0;
		sortOrder = decode(STATE.SORT_ORDER, SORT_ORDER_VALUES,
				DEFAULT_SORT_ORDER);
		refreshMode = decode(STATE.REFRESH_MODE, REFRESH_MODE.VALUES,
				DEFAULT_REFRESH);
		selectedId = currentSelectedId = decode(KEY_SELECTED,
				DEFAULT_SELECTION_ID);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, this);
		}
	}

	/**
	 * Initializes the model as a copy of another model
	 * 
	 * @param aCopy
	 *            the model to copy
	 */
	protected MailListModel(final MailListModel aCopy) {
		super(aCopy);
		bean = aCopy.bean;
		currentSelectedId = aCopy.currentSelectedId;
		curPage = aCopy.curPage;
		pageSize = aCopy.pageSize;
		sortColumn = aCopy.sortColumn;
		sortOrder = aCopy.sortOrder;
		selectedId = aCopy.selectedId;
		refreshMode = aCopy.refreshMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public MailListModel clone() {
		return new MailListModel(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.portal.Disposable#dispose()
	 */
	@Override
	public void dispose() {
		// logging support
		final String LOG_METHOD = "dispose()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// noop for now
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Encodes the stat of the model into a URL
	 * 
	 * @param url
	 *            target URL
	 */
	public void encode(final BaseURL url) {
		// logging support
		final String LOG_METHOD = "encode(url)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { this });
		}
		// encode the nav state
		encode(url, STATE.CURRENT_PAGE, curPage, DEFAULT_PAGE);
		encode(url, STATE.PAGE_SIZE, pageSize, DEFAULT_PAGE_SIZE);
		encode(url, STATE.SORT_COLUMN, sortColumn, DEFAULT_SORT_COLUMN);
		encode(url, STATE.SORT_ORDER, sortOrder, DEFAULT_SORT_ORDER);

		encode(url, KEY_SELECTED, selectedId);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Encodes the stat of the model into a resource URL
	 * 
	 * @param url
	 *            target URL
	 */
	public void encode(final ResourceURL url) {
		// logging support
		final String LOG_METHOD = "encode(url)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { this });
		}
		/**
		 * only encode the mode, because the navigational state is automatically
		 * preserved on the resource URL anyway
		 */
		encode(url, STATE.REFRESH_MODE, refreshMode, DEFAULT_REFRESH);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Encodes the stat of the model into the response. It is unfortunate that
	 * we need to write this method reduntantly to {@link #encode(PortletURL)}
	 * but there exists no common interface between {@link PortletURL} and
	 * {@link StateAwareResponse} that would allow to write parameters using
	 * common code.
	 * 
	 * @param resp
	 *            response
	 */
	public void encode(final StateAwareResponse resp) {
		// logging support
		final String LOG_METHOD = "encode(resp)";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { this });
		}
		// encode the nav state
		encode(resp, STATE.CURRENT_PAGE, curPage, DEFAULT_PAGE);
		encode(resp, STATE.PAGE_SIZE, pageSize, DEFAULT_PAGE_SIZE);
		encode(resp, STATE.SORT_COLUMN, sortColumn, DEFAULT_SORT_COLUMN);
		encode(resp, STATE.SORT_ORDER, sortOrder, DEFAULT_SORT_ORDER);

		encode(resp, KEY_SELECTED, selectedId);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Returns the currently selected page, clipped to the number of available
	 * pages.
	 * 
	 * @return current page
	 * 
	 */
	public int getCurrentPageIdx() {
		// logging support
		final String LOG_METHOD = "getCurrentPageIdx()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// lazily compute the current page
		if (currentPage < 0) {
			// total page count
			currentPage = Math.max(Math.min(curPage, getPageCount() - 1), 0);
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, currentPage);
		}
		// ok
		return currentPage;
	}

	/**
	 * Returns the index of the first visible item on the current page
	 * 
	 * @return index of the item
	 * 
	 */
	public int getFirstItemIdx() {
		// logging support
		final String LOG_METHOD = "getFirstItemIdx()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// lazily compute the index
		if (firstItem < 0) {
			firstItem = getCurrentPageIdx() * getPageSize();
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, firstItem);
		}
		// ok
		return firstItem;
	}

	/**
	 * Returns the index of the first available page
	 * 
	 * @return index of the page
	 * 
	 */
	public int getFirstPageIdx() {
		return 0;
	}

	/**
	 * Access the folder
	 * 
	 * @return current folder
	 * @throws MessagingException
	 */
	private final Folder getFolder() throws MessagingException {
		if (folder == null) {
			folder = bean.getFolder("Inbox");
		}
		return folder;
	}

	/**
	 * Total number of items
	 * 
	 * @return number of items
	 * 
	 */
	public int getItemCount() {
		// logging support
		final String LOG_METHOD = "getItemCount()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// lazily compute the item count
		if (itemCount < 0) {
			try {
				try {
					// count our items
					itemCount = getFolder().getMessageCount();
				} catch (final FolderClosedException ex) {
					// just log
					logException(ex);
					// try again
					itemCount = getFolder().getMessageCount();
				}
			} catch (final Exception ex) {
				// just log
				logException(ex);
				// no items
				itemCount = 0;
			}
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, itemCount);
		}
		// ok
		return itemCount;
	}

	/**
	 * Returns the list of items for the current page
	 * 
	 * @return the item list
	 */
	public List<? extends MessageBean> getItems() {
		// logging support
		final String LOG_METHOD = "getItems()";
		// lazily compute the items
		if (items == null) {
			try {
				// get the folder
				final Folder folder = getFolder();
				if (folder != null) {
					// make sure to open the folder
					if (!folder.isOpen()) {
						folder.open(Folder.READ_ONLY);
					}
					final UIDFolder uidFolder = (UIDFolder) folder;
					// total number of messages
					final int firstIndex = getFirstItemIdx(), lastIndex = getLastItemIdx();
					final Message[] messages = folder.getMessages(
							firstIndex + 1, lastIndex);
					// construct the list
					items = new ArrayList<MessageBean>(messages.length);
					for (final Message message : messages) {
						// add the message
						items.add(bean.resolveMessage(
								bean.getMessageID(message, uidFolder), message));
					}
				} else {
					// fall back to an empty list
					items = Collections.emptyList();
				}
			} catch (final Exception ex) {
				// log this
				LOGGER.logp(Level.SEVERE, LOG_CLASS, LOG_METHOD,
						"Error fetching items.", ex);
				// fall back to an empty list
				items = Collections.emptyList();
			}
		}
		// returns the items
		return items;
	}

	/**
	 * Returns the index after the last visible item on the current page
	 * 
	 * @return index of the item
	 */
	public int getLastItemIdx() {
		// logging support
		final String LOG_METHOD = "getLastItemIdx()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// lazily compute the index
		if (lastItem < 0) {
			lastItem = Math.min(getFirstItemIdx() + getPageSize(),
					getItemCount());
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, lastItem);
		}
		// ok
		return lastItem;
	}

	/**
	 * Returns the index after the last page
	 * 
	 * @return index after the last page
	 */
	public int getLastPageIdx() {
		// logging support
		final String LOG_METHOD = "getLastPageIdx()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		final int result = getPageCount();
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, result);
		}
		// ok
		return result;
	}

	/**
	 * Returns the lates message
	 * 
	 * @return the latest message
	 */
	public MessageBean getLatestMessage() {
		// logging support
		final String LOG_METHOD = "getLatestMessage()";
		// lazily resolve the message
		if (latestMessage == null) {
			try {
				// get the folder
				final UIDFolder folder = (UIDFolder) getFolder();
				final Message msg = folder.getMessageByUID(UIDFolder.LASTUID);
				latestMessage = bean.resolveMessage(folder.getUID(msg), msg);
				// log this
				if (bIsLogging) {
					LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
							"Latest message is [{0}].", latestMessage.getId());
				}
			} catch (final MessagingException ex) {
				// just log
				logException(ex);
			}
		}
		// returns the latest message
		return latestMessage;
	}

	public int getPageCount() {
		// logging support
		final String LOG_METHOD = "getPageCount()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// lazily compute the page count
		if (pageCount < 0) {
			// number of items
			final int itemCount = getItemCount();
			assert itemCount >= 0;
			// page size
			final int pageSize = getPageSize();
			assert pageSize > 0;
			// compute the number of pages
			pageCount = ((itemCount + pageSize) - 1) / pageSize;
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, pageCount);
		}
		// ok
		return pageCount;
	}

	/**
	 * Returns the page size
	 * 
	 * @return page size
	 */
	public int getPageSize() {
		// logging support
		final String LOG_METHOD = "getPageSize()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// returns the page size
		final int result = Math.max(pageSize, MINIMUM_PAGE_SIZE);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, result);
		}
		// ok
		return result;
	}

	/**
	 * Returns the number of page size
	 * 
	 * @return the page sizes
	 */
	public int[] getPageSizes() {
		return PAGE_SIZES;
	}

	/**
	 * Returns the indexes for direct page access. Entries of <code>-1</code>
	 * represent an ellipsis
	 * 
	 * @return the indexes of the quick links
	 * 
	 */
	public long[] getQuickPagesIdx() {
		// logging support
		final String LOG_METHOD = "getQuickPagesIdx()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// lazily create the quick page access
		if (pages == null) {
			// check how many pages we display
			final long pageCount = getPageCount();
			final int quickPageCount = (int) Math.min(pageCount,
					QUICK_PAGE_COUNT);
			switch (quickPageCount) {
			case 0:
				pages = NO_PAGES;
				break;
			case 1:
				pages = ONE_PAGE;
				break;
			case 2:
				pages = TWO_PAGES;
				break;
			default:
				// determine the indexes to display
				long left = getCurrentPageIdx() - (quickPageCount / 2),
				right;
				if (left <= 0) {
					left = 0;
					right = Math.min(left + quickPageCount, pageCount);
				} else {
					right = left + quickPageCount;
					if (right >= pageCount) {
						right = pageCount;
						left = Math.max(right - quickPageCount, 0);
					}
				}
				if (left == 0) {
					if (right == pageCount) {
						pages = new long[(int) (right - left)];
						for (int i = 0; i < pages.length; ++i) {
							pages[i] = left + i;
						}
					} else {
						pages = new long[(int) ((right - left) + 1)];
						for (int i = 0; i < pages.length; ++i) {
							pages[i] = left + i;
						}
						// padding right
						pages[pages.length - 1] = -1;
					}
				} else if (right == pageCount) {
					pages = new long[(int) ((right - left) + 1)];
					for (int i = 1; i < pages.length; ++i) {
						pages[i] = (left + i) - 1;
					}
					// padding right
					pages[0] = -1;
				} else {
					pages = new long[(int) ((right - left) + 2)];
					for (int i = 1; i < pages.length; ++i) {
						pages[i] = (left + i) - 1;
					}
					// padding right
					pages[0] = pages[pages.length - 1] = -1;
				}
				break;
			}
		}
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, Arrays.toString(pages));
		}
		// ok
		return pages;
	}

	/**
	 * Returns indexed access to a date
	 * 
	 * @return map view to the entry
	 */
	public Map<Object, Object> getSelected() {
		if (selectedMap == null) {
			selectedMap = new AbstractJstlMap<Object, Object>() {
				@Override
				protected Object getValue(final Object key) throws Exception {
					// dispatch
					return isSelected((MessageBean) key);
				}
			};

		}
		return selectedMap;
	}

	/**
	 * Returns the currently selected ID
	 * 
	 * @return the selected ID
	 */
	public long getSelectedId() {
		return selectedId;
	}

	/**
	 * Returns the currrent sort column
	 * 
	 * @return
	 * 
	 */
	public int getSortColumn() {
		return sortColumn;
	}

	/**
	 * Returns the current sort order
	 * 
	 * @return the sort order
	 */
	public SORT_ORDER getSortOrder() {
		// sanity check
		assert sortOrder != null;
		// logging support
		final String LOG_METHOD = "getSortOrder()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		// returns the sort order
		final SORT_ORDER result = sortOrder;
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, result);
		}
		// ok
		return result;
	}

	/**
	 * Checks if the application is authenticated
	 * 
	 * @return <code>true</code> if authenticated, else <code>false</code>
	 */
	public boolean isAuthenticated() {
		// logging support
		final String LOG_METHOD = "isAuthenticated()";
		// lazily check if the application is authenticated
		if (bAuthenticated == null) {
			try {
				// try to access the session
				bAuthenticated = getFolder() != null;
			} catch (MessagingException ex) {
				// not authenticated
				bAuthenticated = false;
			}
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD,
						"Authentication [{0}].", bAuthenticated);
			}
		}
		// true for an authenticated session
		return bAuthenticated;
	}

	/**
	 * Checks if we are in mail check mode
	 * 
	 * @return <code>true</code> if we are in mail mode, else <code>false</code>
	 */
	public boolean isCheckMail() {
		return (refreshMode == REFRESH_MODE.MAIL_CHECK);
	}

	/**
	 * Tests if the current page is the first page
	 * 
	 * @return <code>true</code> if the current page is the first page, else
	 *         <code>false</code>
	 * 
	 */
	public boolean isFirstPage() {
		// logging support
		final String LOG_METHOD = "isFirstPage()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		final boolean result = (getCurrentPageIdx() <= getFirstPageIdx());
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, result);
		}
		// ok
		return result;
	}

	/**
	 * Tests if the current page is the last
	 * 
	 * @return <code>true</code> if the current page is the last page, else
	 *         <code>false</code>
	 * 
	 */
	public boolean isLastPage() {
		// logging support
		final String LOG_METHOD = "isLastPage()";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		final boolean result = (getCurrentPageIdx() >= (getLastPageIdx() - 1));
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD, result);
		}
		// ok
		return result;
	}

	/**
	 * Checks if the user is logged in
	 * 
	 * @return <code>true</code> if the user is logged in, else
	 *         <code>false</code>
	 */
	public boolean isLoggedIn() {
		// logging support
		final String LOG_METHOD = "isLoggedIn()";
		// lazily compute the flag
		if (bLoggedIn == null) {
			// check if we have a user
			bLoggedIn = (bean.getCurrentUser() != null);
			// log this
			if (bIsLogging) {
				LOGGER.logp(LOG_LEVEL, LOG_CLASS, LOG_METHOD, "Log in [{0}].",
						bLoggedIn);
			}
		}
		// returns the flag
		return bLoggedIn;
	}

	/**
	 * Checks if we are in refresh mode
	 * 
	 * @return <code>true</code> if we are in refresh mode, else
	 *         <code>false</code>
	 */
	public boolean isRefresh() {
		return (refreshMode == REFRESH_MODE.REFRESH);
	}

	/**
	 * Checks if the given ID represents the selection
	 * 
	 * @param aSelectedId
	 *            the ID
	 * @return <code>true</code> if the ID represents the selection, else
	 *         <code>false</code>
	 */
	public boolean isSelected(final long aSelectedId) {
		return aSelectedId == selectedId;
	}

	/**
	 * Checks if the given ID represents the selection
	 * 
	 * @param aMessage
	 *            the message to check
	 * @return <code>true</code> if the ID represents the selection, else
	 *         <code>false</code>
	 */
	public boolean isSelected(final MessageBean aMessage) {
		return isSelected(aMessage.getId());
	}

	/**
	 * Switches to check mail mode
	 */
	public void setCheckMail() {
		// set to mail checking
		refreshMode = REFRESH_MODE.MAIL_CHECK;
	}

	/**
	 * Assigns the currently selected page
	 * 
	 * @param aCurPage
	 *            current page
	 */
	public void setCurrentPage(final int aCurPage) {
		curPage = aCurPage;
	}

	/**
	 * Moves to the next page
	 * 
	 * 
	 */
	public void setNextPage() {
		setCurrentPage(getCurrentPageIdx() + 1);
	}

	/**
	 * Switches to the next logical sort order
	 */
	public void setNextSortOrder() {
		setSortOrder(sortOrder.getNext());
	}

	/**
	 * Switches to the next logical sort order
	 * 
	 * @param aColumn
	 *            the column to sort on
	 */
	public void setNextSortOrder(final int aColumn) {
		// if the column does not change, switch to the next logical order
		if (sortColumn == aColumn) {
			sortOrder = sortOrder.getNext();
		} else {
			// switch columns and start with the default sort
			sortColumn = aColumn;
			sortOrder = SORT_ORDER.ASCENDING;
		}
	}

	/**
	 * Assigns a new page size
	 * 
	 * 
	 */
	public void setPageSize(final int aSize) {
		pageSize = aSize;
	}

	/**
	 * Moves to the previous page
	 * 
	 * 
	 */
	public void setPreviousPage() {
		setCurrentPage(getCurrentPageIdx() - 1);
	}

	/**
	 * Switches to the previous logical sort order
	 */
	public void setPreviousSortOrder() {
		setSortOrder(sortOrder.getPrevious());
	}

	/**
	 * Switches to refresh
	 */
	public void setRefresh() {
		refreshMode = REFRESH_MODE.REFRESH;
	}

	/**
	 * Replaces the currently selected ID
	 * 
	 * @param aSelectedId
	 *            the selected ID
	 */
	public void setSelectedId(final long aSelectedId) {
		selectedId = aSelectedId;
	}

	/**
	 * Assigns a new sort column
	 * 
	 * @param aColumn
	 *            the sort column
	 */
	public void setSortColumn(final int aColumn) {
		// logging support
		final String LOG_METHOD = "setSortColumn(aColumn)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aColumn });
		}
		// set the sort column
		sortColumn = aColumn;
		setSortOrder(SORT_ORDER.ASCENDING);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Assigns a new sort order
	 * 
	 * @param aOrder
	 *            the sort order
	 */
	public void setSortOrder(final SORT_ORDER aOrder) {
		sortOrder = aOrder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// debug
		return "[curPage: " + curPage + ", pageSize: " + pageSize
				+ ", sortColumn: " + sortColumn + ", sortOrder: " + sortOrder
				+ "]";
	}
}
