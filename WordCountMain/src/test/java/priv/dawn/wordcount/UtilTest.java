package priv.dawn.wordcount;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @Auther: Dawn Yang
 * @Since: 2024/10/16/20:24
 */
public class UtilTest {

    @Test
    public void tokenTest() {
        String context = "【环球时报报道 记者 郭媛丹 刘欣】中国本月14日第三次发布报告揭露美方制造所谓“伏特台风”真实意图。尽管被中国官方报告“点名”，但截至本报16日凌晨发稿，美国驻华大使馆和微软公司没有回应《环球时报》的相关问询，继续保持沉默。同时，据彭博社报道，美国国务院和微软也未对其问询作出回应。\n" +
                "\n" +
                "14日，由中国国家计算机病毒应急处理中心等部门联合发布的最新报告披露了美方集中炒作名为“伏特台风”的组织，通过嫁祸他国掩盖自身网络攻击行为。这是中国第三次发布报告，在今年4月15日和7月8日，中方两次披露所谓“伏特台风”实际上是一个国际勒索软件组织，美国情报机构和网络安全企业为获取国会的预算拨款、政府合同，合谋散布虚假信息、栽赃中国。\n" +
                "\n" +
                "在前两个调查报告发布后，美国联邦政府机构、主流媒体和微软公司集体保持沉默，但以罗伯特·乔伊斯等人为代表的前任和现任美国情报机构及美国网络安全主管部门官员，以及部分美国网络安全企业和网络安全媒体出来发声谈及所谓的“伏特台风”仍在实施网络攻击，却避而不谈中方在前两个报告公布的证据。\n" +
                "\n" +
                "在最新报告发布后，《环球时报》记者向美国驻华大使馆和微软公司发送邮件，询问他们如何回应报告提到的美国和微软编造有关“伏特台风”的虚假叙事，以及美方集中炒作名为“伏特台风”的组织，通过嫁祸他国来掩盖自身的网络攻击行为。截至本报发稿，双方均未作出回应。\n" +
                "\n" +
                "北京外国语大学国际关系学院国际问题专家卓华15日对《环球时报》记者表示，美国政府找不到可以辩驳的理由，只能沉默。“无论是过去在全球范围内针对包括盟友在内的国家的网络监视和窃密活动，还是针对中国的虚假信息行动，事实都非常清楚，这种国家支持的大规模网络威胁活动甚至可以上升到网络恐怖主义的高度，即便面对铁证，美国政府官方也不可能表态承认。”\n" +
                "\n" +
                "报告提及，“报告发布后，先后有来自美国、欧洲、亚洲等国家和地区的50余位网络安全专家通过各种方式与我中心联系，认为美国和微软公司将‘伏特台风’与中国政府关联，缺乏有效证据。”对此，卓华表示，国外民众对这场美国虚假信息活动的关注，说明“棱镜门”等事件暴露多年来，尽管政府竭力隐藏压制来淡化处理，但是西方民众对美国政府的不信任仍然没有修复，他们希望看到当前国际网络安全的事实和真相。";

        int begin = 0;
        int len = 200;
        int id = 1;

        Segment segment = HanLP.newSegment().enableOffset(true);

        while (begin < context.length() - len) {

            int fromIndex = begin + len;
            int theBound = Math.min(context.length(), fromIndex + 50);
            List<Term> tokens = segment.seg(context.substring(fromIndex, theBound));
            int end = tokens.get(1).offset + fromIndex;
            System.out.println("begin = " + begin);
            System.out.println("tokens.get(0).word = " + tokens.get(0).word);
            System.out.println("segment.get(1).offset = " + tokens.get(1).offset);
            System.out.println("tokens.get(1).word = " + tokens.get(1).word);
            String chunk = context.substring(begin, end);
            System.out.println("chunk = " + chunk);
            begin = end;
        }
        String chunk = context.substring(begin);
        System.out.println("chunk = " + chunk);
//        int fromIndex = begin + len;
//        List<Term> segment = StandardTokenizer.segment(context.substring(fromIndex));
//        int end = segment.get(1).offset + fromIndex;

    }
}
