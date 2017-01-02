package org.bibliome.util.taxonomy.saturate;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bibliome.util.taxonomy.Name;

/**
 * Saturates names according to regular expression replacements.
 * @author rbossy
 *
 */
public class SaturatePattern implements Saturate {
	private final Pattern pattern;
	private final String type;
	private final Collection<MessageFormat> formats;

	/**
	 * Creates a name saturator that recognizes the specified regular expression.
	 * @param pattern
	 * @param type
	 * @param formats
	 */
	public SaturatePattern(Pattern pattern, String type, Collection<MessageFormat> formats) {
		super();
		this.pattern = pattern;
		this.type = type;
		this.formats = formats;
	}

	@Override
	public Collection<Name> saturate(Name name) {
		Matcher m = pattern.matcher(name.name);
		if (!m.matches())
			return Collections.emptyList();
		String[] groups = new String[m.groupCount() + 1];
		for (int i = 0; i < groups.length; ++i)
			groups[i] = m.group(i);
		Collection<Name> result = new ArrayList<Name>(formats.size());
		for (MessageFormat fmt : formats) {
			String s = fmt.format(groups);
			result.add(new Name(s, type));
		}
		return result;
	}
}
