package com.facilio.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EmailMessageParser {
	
	public static void main(String[] args) {
		
		String s = "Client Reply new\n"
		+ "\n"
		+ "On Mon, Apr 5, 2021 at 11:55 AM Facilio inc <\n"
		+ "support@newaccount1616644875777.freshdesk.com> wrote:\n"
		+ "\n"
		+ "> Hi krishnan E,\n"
		+ ">\n"
		+ "> Admin reply 2222\n"
		+ ">\n"
		+ "> On Mon, 5 Apr at 2:16 AM , Facilio inc <\n"
		+ "> support@newaccount1616644875777.freshdesk.com> wrote:\n"
		+ "> test quoted\n"
		+ ">\n"
		+ "> On Mon, Apr 5, 2021 at 11:41 AM Facilio inc <\n"
		+ "> support@newaccount1616644875777.freshdesk.com> wrote:\n"
		+ ">\n"
		+ ">> Hi krishnan E,\n"
		+ ">>\n"
		+ ">> Admin reply 1\n"
		+ ">>\n"
		+ ">> On Mon, 5 Apr at 2:07 AM , krishnan E <krishnan.e@facilio.com> wrote:\n"
		+ ">> *desc italic*\n"
		+ ">>\n"
		+ ">> --\n"
		+ ">> *Krishnan E*\n"
		+ ">> Member Technical Staff\n"
		+ ">> *_____________________*\n"
		+ ">>\n"
		+ ">>\n"
		+ ">> m: +919677096980\n"
		+ ">> e: krishnan.e@facilio.com\n"
		+ ">>\n"
		+ ">>\n"
		+ ">> Facilio inc powered by Freshdesk\n"
		+ ">> <https://freshdesk.com/freshdesk-demo?utm_source=emailfooter&referrer=newaccount1616644875777.freshdesk.com>\n"
		+ ">>\n"
		+ ">\n"
		+ ">\n"
		+ "> --\n"
		+ "> *Krishnan E*\n"
		+ "> Member Technical Staff\n"
		+ "> *_____________________*\n"
		+ ">\n"
		+ ">\n"
		+ "> m: +919677096980\n"
		+ "> e: krishnan.e@facilio.com\n"
		+ ">\n"
		+ ">\n"
		+ "> Facilio inc powered by Freshdesk\n"
		+ "> <https://freshdesk.com/freshdesk-demo?utm_source=emailfooter&referrer=newaccount1616644875777.freshdesk.com>\n"
		+ "> 14:1874477\n"
		+ ">\n"
		+ "\n"
		+ "\n"
		+ "-- \n"
		+ "*Krishnan E*\n"
		+ "Member Technical Staff\n"
		+ "*_____________________*\n"
		+ "\n"
		+ "\n"
		+ "m: +919677096980\n"
		+ "e: krishnan.e@facilio.com";
		EmailMessageParser emailMessager = EmailMessageParser.read(s);
		
		System.out.println("reply -- "+emailMessager.getReply());
	}

	private final static CharMatcher NEW_LINE = CharMatcher.anyOf("\n");
	private final static CharMatcher CRLF = CharMatcher.anyOf("\r\n");

	private final static Pattern SIG_REGEX = Pattern.compile("(\u2014|--|__|-\\w)|(^Sent from my (\\w+\\s*){1,3})");
	private final static Pattern QUOTE_HDR_REGEX = Pattern.compile("^:etorw.*nO");
	private final static Pattern MULTI_QUOTE_HDR_REGEX = Pattern.compile("(?!On.*On\\s.+?wrote:)(On\\s(.+?)wrote:)");
	private final static Pattern QUOTED_REGEX = Pattern.compile("(>+)");
	private String text = "";
	private List<Fragment> fragments = Lists.newArrayList();
	private boolean foundVisible = false;
	private Fragment fragment;

	public EmailMessageParser(String text) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(text));
		this.text = CRLF.replaceFrom(text, "\n");
	}

	public static EmailMessageParser read(String content) {
		EmailMessageParser message = new EmailMessageParser(content);
		message.read();
		return message;
	}

	public static String parseReply(String content) {
		EmailMessageParser message = read(content);
		return message.getReply();
	}

	public List<Fragment> getFragments() {
		return fragments;
	}

	public String getReply() {
		return fragments.stream().filter(f -> !(f.isHidden() || f.isQuoted()))
				.map(f -> f.getContent()).collect(Collectors.joining("\n"));
	}

	public String getSignature() {
		return fragments.stream().filter(f -> (f.isSignature())).map(f -> f.getContent())
				.collect(Collectors.joining("\n"));
	}

	public String getHidden() {
		return fragments.stream().filter(f -> (f.isHidden())).map(f -> f.getContent())
				.collect(Collectors.joining("\n"));
	}
	
	public String getQuoted() {
		return fragments.stream().filter(f -> (f.isQuoted())).map(f -> f.getContent())
				.collect(Collectors.joining("\n"));
	}

	public void read() {
		String workingText = text;
		Matcher multiQuote = Pattern.compile(MULTI_QUOTE_HDR_REGEX.pattern(), Pattern.MULTILINE | Pattern.DOTALL)
				.matcher(workingText);

		if (multiQuote.find()) {
			String newQuoteHeader = NEW_LINE.replaceFrom(multiQuote.group(), "");
			workingText = Pattern.compile(MULTI_QUOTE_HDR_REGEX.pattern(), Pattern.DOTALL).matcher(workingText)
					.replaceAll(newQuoteHeader);
		}

		Lists.reverse(Splitter.on('\n').splitToList(workingText)).stream().forEach(l -> scanLine(l));

		finishFragment();

		fragments = ImmutableList.copyOf(Lists.reverse(fragments));
	}

	private void scanLine(String line) {
		line = NEW_LINE.trimFrom(line);
		if (SIG_REGEX.matcher(line).lookingAt()) {
			line = NEW_LINE.trimLeadingFrom(line);
		}
		boolean isQuoted = QUOTED_REGEX.matcher(line).lookingAt();

		if (fragment != null && isStringEmpty(line)) {
			if (SIG_REGEX.matcher(fragment.lines.get(fragment.lines.size() - 1)).lookingAt()) {
				fragment.setSignature(true);
				finishFragment();
			}
		}

		if (fragment != null && ((fragment.isQuoted() == isQuoted)
				|| (fragment.isQuoted() && (quoteHeader(line) || isStringEmpty(line))))) {
			fragment.lines.add(line);
		} else {
			finishFragment();
			fragment = new Fragment(isQuoted, line);
		}

	}

	private boolean quoteHeader(String line) {
		String reversed = new StringBuffer(line).reverse().toString();
		return QUOTE_HDR_REGEX.matcher(reversed).lookingAt();
	}

	private void finishFragment() {
		if (fragment != null) {
			fragment.finish();
			if (!foundVisible) {
				if (fragment.isQuoted() || fragment.isSignature() || isStringEmpty(fragment.getContent())) {
					fragment.setHidden(true);
				} else {
					foundVisible = true;
				}
			}
			fragments.add(fragment);
		}

		fragment = null;
	}

	private boolean isStringEmpty(String content) {
		return CharMatcher.whitespace().trimFrom(content).isEmpty();
	}

	public static class Fragment {

		private boolean signature;
		private boolean hidden;
		private boolean quoted;
		private String content;
		private List<String> lines = Lists.newArrayList();

		public Fragment(boolean quoted, String firstLine) {
			this.quoted = quoted;
			this.lines.add(firstLine);
		}

		public String getContent() {
			return content;
		}

		public boolean isSignature() {
			return signature;
		}

		public void setSignature(boolean signature) {
			this.signature = signature;
		}

		public boolean isHidden() {
			return hidden;
		}

		public void setHidden(boolean hidden) {
			this.hidden = hidden;
		}

		public boolean isQuoted() {
			return quoted;
		}

		public void finish() {
			content = CharMatcher.whitespace()
					.trimFrom(Lists.reverse(lines).stream().collect(Collectors.joining("\n")));
			lines = ImmutableList.of();
		}
	}
}
