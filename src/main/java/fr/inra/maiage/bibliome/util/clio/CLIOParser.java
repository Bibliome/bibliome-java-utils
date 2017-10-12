/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package fr.inra.maiage.bibliome.util.clio;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import fr.inra.maiage.bibliome.util.Strings;

/**
 * Base class for a command line argument parser.
 * @author rbossy
 *
 */
public abstract class CLIOParser {
	private final Map<Class<?>,CLIOConverter> converters = new HashMap<Class<?>,CLIOConverter>();

	/**
	 * Creates a command line argument parser.
	 * Converters from StandardConverters are automatically registerred.
	 */
	protected CLIOParser() {
		for (StandardConverters conv : StandardConverters.values())
			registerConverter(conv);
	}
	
	private Map<String,Method> getOptions() {
		Map<String,Method> result = new TreeMap<String,Method>();
		Class<?> klass = getClass();
		Class<CLIOption> optionClass = CLIOption.class;
		for (Method meth : klass.getMethods()) {
			if (!meth.isAnnotationPresent(optionClass))
				continue;
			CLIOption annotation = meth.getAnnotation(optionClass);
			String option = annotation.value();
			if (result.containsKey(option))
				throw new RuntimeException("duplicate option " + option);
			result.put(option, meth);
		}
		return result;
	}
	
	@SuppressWarnings("serial")
	private static final class OptionOrder extends HashMap<String,Integer> implements Comparator<String> {
		private OptionOrder() {
			super();
		}

		@Override
		public int compare(String a, String b) {
			return Integer.compare(get(a), get(b));
		}
	}
	
	private static Map<String,List<String>> getGroups(ResourceBundle bundle, Collection<String> options) {
		Map<String,List<String>> result = new LinkedHashMap<String,List<String>>();
		OptionOrder order = new OptionOrder();
		String groups = getBundleString(bundle, "groups", "options");
		for (String g : Strings.splitAndTrim(groups, ',', -1))
			result.put(g, new ArrayList<String>());
		for (String o : options) {
			for (String g : Strings.splitAndTrim(getBundleString(bundle, o + ".group", "options"), ',', -1)) {
				if (!result.containsKey(g))
					throw new RuntimeException("option " + o + " belongs to unknown group " + g);
				result.get(g).add(o);
			}
			String ord = getBundleString(bundle, o + ".order", Integer.MAX_VALUE);
			order.put(o, Integer.parseInt(ord));
		}
		for (List<String> opts : result.values())
			Collections.sort(opts, order);
		return result;
	}
	
	/**
	 * Parse the specified command line.
	 * @param args
	 * @return either one option interrupted the parsing
	 * @throws CLIOException
	 */
	public boolean parse(String[] args) throws CLIOException {
		Map<String,Method> options = getOptions();
		Iterator<String> argIt = Arrays.asList(args).iterator();
		while (argIt.hasNext()) {
			String arg = argIt.next();
			boolean stop;
			if (options.containsKey(arg))
				stop = processOption(argIt, arg, options.get(arg));
			else
				stop = processArgument(arg);
			if (stop) {
				if (argIt.hasNext())
					throw new CLIOException("trailing arguments");
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Process a trailing argument.
	 * @param arg
	 * @return either CL parsing should stop
	 * @throws CLIOException
	 */
	protected abstract boolean processArgument(String arg) throws CLIOException;

	private boolean processOption(Iterator<String> argIt, String option, Method method) throws CLIOException {
		Class<?>[] parameterTypes = method.getParameterTypes();
		Object[] parameters = new Object[parameterTypes.length];
		for (int i = 0; i < parameters.length; ++i) {
			if (!argIt.hasNext())
				throw new RuntimeException("missing argument " + (i+1) + " for option " + option);
			parameters[i] = convertArgument(parameterTypes[i], argIt.next());
		}
		try {
			method.invoke(this, parameters);
		}
		catch (IllegalArgumentException iae) {
			throw new CLIOException(iae);
		}
		catch (IllegalAccessException iae) {
			throw new CLIOException(iae);
		}
		catch (InvocationTargetException ite) {
			throw new CLIOException(ite);
		}
		return method.getAnnotation(CLIOption.class).stop();
	}

	/**
	 * Convert the specified string to the specified type.
	 * @param klass
	 * @param arg
	 * @throws CLIOConversionException if there is no converter registered for the specified type, or if the specified string could not be converted
	 */
	public Object convertArgument(Class<?> klass, String arg) throws CLIOConversionException {
		if (!converters.containsKey(klass))
			throw new CLIOConversionException("cannot convert " + arg + " into " + klass.getName());
		return converters.get(klass).convert(arg);
	}

	/**
	 * Register the specified converter.
	 * @param converter
	 */
	protected void registerConverter(CLIOConverter converter) {
		for (Class<?> klass : converter.targetTypes())
			converters.put(klass, converter);
	}
	
	private static String getBundleString(ResourceBundle bundle, String key, Object def) {
		if (bundle.containsKey(key))
			return bundle.getString(key);
		return def.toString();
	}
	
	private static void usageSection(StringBuilder sb, ResourceBundle bundle, String title, String contents, Object defaultTitle, Object defaultContents) {
		sb.append(getBundleString(bundle, title, defaultTitle));
		sb.append('\n');
		if (contents == null)
			return;
		sb.append("    ");
		sb.append(getBundleString(bundle, contents, defaultContents));
		sb.append('\n');
		sb.append('\n');
	}
	
	private void usageOptions(StringBuilder sb, ResourceBundle bundle) {
		Map<String,Method> allOptions = getOptions();
		Map<String,List<String>> groups = getGroups(bundle, allOptions.keySet());
		for (Map.Entry<String,List<String>> ge : groups.entrySet()) {
			String group = ge.getKey();
			Collection<String> options = ge.getValue();
			List<Object[]> table = new ArrayList<Object[]>(options.size());
			int maxOptionSize = 0;
			int maxArgsSize = 1;
			for (String option : options) {
				Method meth = allOptions.get(option);
				maxOptionSize = Math.max(maxOptionSize, option.length());
				String args = getBundleString(bundle, option + ".args", defaultOptionArgs(meth));
				maxArgsSize = Math.max(maxArgsSize, args.length());
				Object[] row  = new String[] {
						option,
						args,
						getBundleString(bundle, option + ".help", "undocumented")
				};
				table.add(row);
			}
			usageSection(sb, bundle, group, null, "Options:", null);
			String format = "    %-" + maxOptionSize + "s %-" + maxArgsSize + "s    %s\n";
			Formatter formatter = new Formatter(sb, bundle.getLocale());
			for (Object[] row : table)
				formatter.format(format, row);
			sb.append('\n');
			formatter.close();
		}
	}

	private static String defaultOptionArgs(Method meth) {
		Class<?>[] paramTypes = meth.getParameterTypes();
		String[] paramTypeStrings = new String[paramTypes.length];
		for (int i = 0; i < paramTypes.length; ++i)
			paramTypeStrings[i] = paramTypes[i].getSimpleName().toUpperCase();
		return Strings.join(paramTypeStrings, ' ');
	}

	/**
	 * Returns an usage message using the specified locale.
	 * @param locale
	 */
	public String usage(Locale locale) {
		StringBuilder sb = new StringBuilder();
		ResourceBundle bundle = ResourceBundle.getBundle(getResourceBundleName(), locale);
		
		usageSection(sb, bundle, "prog", "synopsis", getClass(), "???");
		usageSection(sb, bundle, "usage", "usageMessage", "Usage:", "???");
		usageOptions(sb, bundle);
		
		return sb.toString();
	}
	
	/**
	 * Returns an usage message using the default locale.
	 */
	public String usage() {
		return usage(Locale.getDefault());
	}

	/**
	 * Returns the name of the resource bundle containing options documentation and message strings.
	 */
	public abstract String getResourceBundleName();
}
