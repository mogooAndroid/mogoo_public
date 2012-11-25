package com.michelin.droid.parsers;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.michelin.droid.Droid;
import com.michelin.droid.error.DroidError;
import com.michelin.droid.error.DroidParseException;
import com.michelin.droid.types.Apk;

public class ApkParser extends AbstractParser<Apk> {
    private static final Logger LOG = Logger.getLogger(ApkParser.class.getCanonicalName());
    private static final boolean DEBUG = Droid.PARSER_DEBUG;
    /*    	
	<rc id='13544'>
		<n><![CDATA[墨迹天气]]></n>
		<vn><![CDATA[2.21.04]]></vn>
		<v>22104</v>
		<s>6101676</s>
		<pn><![CDATA[com.moji.mjweather]]></pn>
		<xc>2.33333</xc>
		<zc>2.33333</zc>
		<p>0.0</p>
		<a><![CDATA[墨迹风云（北京）软件科技发展有限公司 ]]></a>
		<ap><![CDATA[http://www.imogoo.cn/FS/upload/store/app/apk/2012/08/13450162230336216.apk]]></ap>
		<icp><![CDATA[http://www.imogoo.cn/FS/upload/store/app/images/2012/08/icon_1345016222914435.png]]></icp>
	</rc>
	*/
    @Override
    public Apk parseInner(XmlPullParser parser) throws XmlPullParserException, IOException,
            DroidError, DroidParseException {
		Apk apk = null;
		int eventType = parser.getEventType();
		while (eventType == XmlPullParser.START_TAG) {
			String name = parser.getName();
			if ("rc".equals(name)) {
				apk = new Apk();
				apk.setId(parser.getAttributeValue(0));
			} else if ("n".equals(name)) {
				apk.setName(parser.nextText());
			} else if ("vn".equals(name)) {
				apk.setVersionName(parser.nextText());
			} else if ("v".equals(name)) {
				apk.setVersionCode(parser.nextText());
			} else if ("s".equals(name)) {
				apk.setSize(parser.nextText());
			} else if ("pn".equals(name)) {
				apk.setPackageName(parser.nextText());
			} else if ("xc".equals(name)) {
				apk.setVirtualScore(parser.nextText());
			} else if ("zc".equals(name)) {
				apk.setRealScore(parser.nextText());
			} else if ("p".equals(name)) {
				apk.setPrice(parser.nextText());
			} else if ("a".equals(name)) {
				apk.setAuthor(parser.nextText());
			} else if ("ap".equals(name)) {
				apk.setApkUrl(parser.nextText());
			} else if ("icp".equals(name)) {
				apk.setIconUrl(parser.nextText());
			} else {
				// Consume something we don't understand.
				if (DEBUG)
					LOG.log(Level.FINE, "Found tag that we don't recognize: "
							+ name);
				skipSubTree(parser);
			}
			eventType = parser.nextTag();
		}
		return apk;
    }
}
