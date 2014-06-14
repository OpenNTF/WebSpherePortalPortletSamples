package com.ibm.portal.samples.mail.list.view;

import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.portlet.MimeResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;

import com.ibm.portal.samples.mail.common.AbstractView;
import com.ibm.portal.samples.mail.helper.AbstractJstlMap;
import com.ibm.portal.samples.mail.list.model.MailListModel;

/**
 * Bean that represents formatting related aspects. We prefer to use a bean over
 * the JSTL fmt tags, because the tags do not directly support escaping and it
 * is tedious and error prone to copy every string into a temporary variable in
 * the JSP just to escape it later. The JSP becomes more readable using the bean
 * approach.
 * 
 * @author cleue
 */
public class MailListView extends AbstractView {

	/** class name for the logger */
	private static final String LOG_CLASS = MailListView.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * model used for rendering
	 */
	private final MailListModel model;

	/**
	 * logging support, we can do this as a instance variable since the model
	 * bean is instantiated for every request
	 */
	private final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);

	/**
	 * Primary Paging
	 */
	private String pagingPrimary;

	/**
	 * Showing items {0} through {1} of {2}
	 */
	private String pagingShowingLarge;

	/**
	 * {0} - {1} of {2}
	 */
	private String pagingShowingSmall;

	/**
	 * Go to the previous page
	 */
	private String pagingPreviousPageTitle;

	/**
	 * Jump to page
	 */
	private String pagingJumpPrefix;

	/**
	 * of {0}
	 */
	private String pagingJumpSuffix;

	/**
	 * Jump to a page between {0} and {1}
	 */
	private String pagingJumpLarge;

	/**
	 * Previous
	 */
	private String pagingPreviousPage;

	/**
	 * Go to the next page
	 */
	private String pagingNextPageTitle;

	/**
	 * Login to portal
	 */
	private String unauthenticatedMessage;

	/**
	 * Next
	 */
	private String pagingNextPage;

	/**
	 * Jump to page {0}
	 */
	private Map<Object, Object> pagingQuickPageTitle;

	/**
	 * date formatting
	 */
	private Map<Object, Object> dateMap;

	/**
	 * Page size formatting
	 */
	private Map<Object, Object> pagingPageSize;

	/**
	 * Reverse sort
	 */
	private String sortReverse;

	/**
	 * Show:
	 */
	private String pagingShowPrefix;

	/**
	 * items
	 */
	private String pagingShowSuffix;

	/**
	 * Initialize this view bean
	 * 
	 * @param aConfig
	 *            portlet config, used to access the resource bundle
	 * @param aResponse
	 *            the response, used to access the desired locale
	 * @param aModel
	 *            model
	 */
	public MailListView(final PortletConfig aConfig,
			final PortletRequest aRequest, final MimeResponse aResponse,
			final MailListModel aModel) {
		// default init
		super(aConfig, aRequest, aResponse);
		// logging support
		final String LOG_METHOD = "MailListView(aConfig, aRequest, aResponse, aModel)";
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD, new Object[] { aConfig,
					aRequest, aResponse, aModel });
		}
		// init
		model = aModel;
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
	}

	/**
	 * Returns indexed access to a formatted date
	 * 
	 * @return map view to the date
	 */
	public Map<Object, Object> getDate() {
		if (dateMap == null) {
			dateMap = new AbstractJstlMap<Object, Object>() {
				@Override
				protected Object getValue(Object key) throws Exception {
					// dispatch
					return getDate((Date) key);
				}
			};

		}
		return dateMap;
	}

	/**
	 * @return "Jump to a page between {0} and {1}"
	 * 
	 */
	public String getPagingJumpLarge() {
		if (pagingJumpLarge == null) {
			pagingJumpLarge = formatMessage("paging.jumpLarge",
					model.getFirstPageIdx() + 1, model.getPageCount());
		}
		return pagingJumpLarge;
	}

	/**
	 * @return "Jump to page"
	 */
	public String getPagingJumpPrefix() {
		if (pagingJumpPrefix == null) {
			pagingJumpPrefix = getMessage("paging.jump.prefix");
		}
		return pagingJumpPrefix;
	}

	/**
	 * @return "of {0}"
	 * 
	 */
	public String getPagingJumpSuffix() {
		if (pagingJumpSuffix == null) {
			pagingJumpSuffix = formatMessage("paging.jump.suffix",
					model.getPageCount());
		}
		return pagingJumpSuffix;
	}

	/**
	 * @return "Next"
	 */
	public String getPagingNextPage() {
		if (pagingNextPage == null) {
			pagingNextPage = getMessage("paging.nextPage");
		}
		return pagingNextPage;
	}

	/**
	 * @return "Go to the next page"
	 */
	public String getPagingNextPageTitle() {
		if (pagingNextPageTitle == null) {
			pagingNextPageTitle = getMessage("paging.nextPage.title");
		}
		return pagingNextPageTitle;
	}

	/**
	 * Returns indexed access to the page size string
	 * 
	 * @return map view to the string
	 */
	public Map<Object, Object> getPagingPageSize() {
		if (pagingPageSize == null) {
			pagingPageSize = new AbstractJstlMap<Object, Object>() {
				@Override
				protected Object getValue(Object key) throws Exception {
					// dispatch
					return getPagingPageSize(((Number) key).intValue());
				}
			};

		}
		return pagingPageSize;
	}

	/**
	 * Formats the string for the page size. Important, make sure that this is a
	 * private method or do not use the bean naming convention. Otherwise JSTL
	 * will recognize this as an indexed getter method and overlay the
	 * {@link #getPagingPageSize()} method. This recognition only happens if the
	 * index is of type <code>int</code> but for consistency reasons we make all
	 * methods that the helper maps delegate to private.
	 * 
	 * @param aSize
	 *            page size
	 * @return the formatted string
	 */
	private final String getPagingPageSize(final int aSize) {
		return formatMessage("paging.size", aSize);
	}

	/**
	 * @return "Previous"
	 */
	public String getPagingPreviousPage() {
		if (pagingPreviousPage == null) {
			pagingPreviousPage = getMessage("paging.previousPage");
		}
		return pagingPreviousPage;
	}

	/**
	 * @return "Go to the previous page"
	 */
	public String getPagingPreviousPageTitle() {
		if (pagingPreviousPageTitle == null) {
			pagingPreviousPageTitle = getMessage("paging.previousPage.title");
		}
		return pagingPreviousPageTitle;
	}

	/**
	 * @return "Primary Paging"
	 */
	public String getPagingPrimary() {
		if (pagingPrimary == null) {
			pagingPrimary = getMessage("paging.primary");
		}
		return pagingPrimary;
	}

	/**
	 * Returns indexed access to the translated string
	 * 
	 * @return map view to the translation
	 */
	public Map<Object, Object> getPagingQuickPageTitle() {
		if (pagingQuickPageTitle == null) {
			pagingQuickPageTitle = new AbstractJstlMap<Object, Object>() {
				@Override
				protected Object getValue(Object key) throws Exception {
					// dispatch
					return getPagingQuickPageTitle(((Number) key).longValue());
				}
			};

		}
		return pagingQuickPageTitle;
	}

	/**
	 * 
	 * @param aPage
	 *            page index, zero based
	 * @return "Jump to page {0}"
	 */
	private final String getPagingQuickPageTitle(final long aPage) {
		return formatMessage("paging.quickPage.title", aPage + 1);
	}

	/**
	 * @return "Showing items {0} through {1} of {2}"
	 * 
	 */
	public String getPagingShowingLarge() {
		if (pagingShowingLarge == null) {
			pagingShowingLarge = formatMessage("paging.showingLarge",
					model.getFirstItemIdx() + 1, model.getLastItemIdx(),
					model.getItemCount());
		}
		return pagingShowingLarge;
	}

	/**
	 * @return {0} - {1} of {2}
	 * 
	 */
	public String getPagingShowingSmall() {
		if (pagingShowingSmall == null) {
			pagingShowingSmall = formatMessage("paging.showingSmall",
					model.getFirstItemIdx() + 1, model.getLastItemIdx(),
					model.getItemCount());
		}
		return pagingShowingSmall;
	}

	/**
	 * @return "Show:"
	 */
	public String getPagingShowPrefix() {
		if (pagingShowPrefix == null) {
			pagingShowPrefix = getMessage("paging.show.prefix");
		}
		return pagingShowPrefix;
	}

	/**
	 * @return "items"
	 */
	public String getPagingShowSuffix() {
		if (pagingShowSuffix == null) {
			pagingShowSuffix = getMessage("paging.show.suffix");
		}
		return pagingShowSuffix;
	}

	/**
	 * @return "Reverse sort"
	 */
	public String getSortReverse() {
		if (sortReverse == null) {
			sortReverse = getMessage("sort.reverse");
		}
		return sortReverse;
	}

	/**
	 * auth.title
	 */
	private String authTitle;

	/**
	 * @return "Mail Credentials"
	 */
	public String getAuthenticationTitle() {
		if (authTitle == null) {
			authTitle = getMessage("auth.title");
		}
		return authTitle;
	}

	/**
	 * auth.desc
	 */
	private String authDesc;

	/**
	 * @return "Mail Credentials"
	 */
	public String getAuthenticationDescription() {
		if (authDesc == null) {
			authDesc = getMessage("auth.desc");
		}
		return authDesc;
	}

	/**
	 * auth.username
	 */
	private String username;

	/**
	 * @return "Username:"
	 */
	public String getUsername() {
		if (username == null) {
			username = getMessage("auth.username");
		}
		return username;
	}

	/**
	 * auth.password
	 */
	private String password;

	/**
	 * @return "Username:"
	 */
	public String getPassword() {
		if (password == null) {
			password = getMessage("auth.password");
		}
		return password;
	}

	/**
	 * @return "Please login to view mails."
	 */
	public String getUnauthenticatedMessage() {
		if (unauthenticatedMessage == null) {
			unauthenticatedMessage = getMessage("auth.unauthenticated");
		}
		return unauthenticatedMessage;
	}

	/**
	 * field.required
	 */
	private String requiredField;

	/**
	 * @return "Required field"
	 */
	public String getRequiredField() {
		if (requiredField == null) {
			requiredField = getMessage("field.required");
		}
		return requiredField;
	}

	/**
	 * auth.save
	 */
	private String saveButton;

	/**
	 * @return "Save..."
	 */
	public String getSaveButton() {
		if (saveButton == null) {
			saveButton = getMessage("auth.save");
		}
		return saveButton;
	}
}
