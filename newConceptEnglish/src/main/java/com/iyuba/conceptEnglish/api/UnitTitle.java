package com.iyuba.conceptEnglish.api;

import com.google.gson.Gson;

import java.util.List;

public class UnitTitle {

    /**
     * size : 144
     * data : [{"voa_id":1001,"title":"Excuse me!","title_cn":"对不起！","text_num":10,"end_time":10,"choice_num":5},{"voa_id":1002,"title":"Is this your ... ?","title_cn":"这是你的\u2026\u2026吗？","text_num":20,"end_time":20,"choice_num":0},{"voa_id":1003,"title":"Sorry, sir.","title_cn":"对不起，先生。","text_num":15,"end_time":15,"choice_num":5},{"voa_id":1004,"title":"Is this your ... ?","title_cn":"这是你的\u2026\u2026吗？","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1005,"title":"Nice to meet you.","title_cn":"很高兴见到你。","text_num":23,"end_time":23,"choice_num":5},{"voa_id":1006,"title":"What make is it?","title_cn":"它是什么牌子的？","text_num":32,"end_time":32,"choice_num":0},{"voa_id":1007,"title":"Are you a teacher?","title_cn":"你是教师吗？","text_num":19,"end_time":19,"choice_num":5},{"voa_id":1008,"title":"What's your job?","title_cn":"你是做什么工作的？","text_num":40,"end_time":40,"choice_num":0},{"voa_id":1009,"title":"How are you today?","title_cn":"你今天好吗？","text_num":17,"end_time":17,"choice_num":5},{"voa_id":1010,"title":"Look at...","title_cn":"看\u2026\u2026","text_num":20,"end_time":20,"choice_num":0},{"voa_id":1011,"title":"Is this your shirt?","title_cn":"这是你的衬衫吗？","text_num":19,"end_time":19,"choice_num":5},{"voa_id":1012,"title":"Whose is this\u2026?This is my/your/his/her\u2026Whose is that\u2026?That is my/your/his/her...","title_cn":"这\u2026\u2026是谁的？这是我的/你的/他的/她的\u2026\u2026那\u2026\u2026是谁的？那是我的/你的/他的/她的\u2026\u2026","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1013,"title":"A new dress","title_cn":"一件新连衣裙","text_num":15,"end_time":15,"choice_num":5},{"voa_id":1014,"title":"What colour is your...?","title_cn":"你的\u2026\u2026是什么颜色的？","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1015,"title":"Your passports, please.","title_cn":"请出示你们的护照。","text_num":21,"end_time":21,"choice_num":5},{"voa_id":1016,"title":"Are you...?","title_cn":"你是\u2026\u2026吗？","text_num":31,"end_time":31,"choice_num":0},{"voa_id":1017,"title":"How do you do?","title_cn":"你好！","text_num":18,"end_time":18,"choice_num":5},{"voa_id":1018,"title":"What are their jobs?","title_cn":"他们是做什么的？","text_num":40,"end_time":40,"choice_num":0},{"voa_id":1019,"title":"Tired and thirsty","title_cn":"又累又渴","text_num":15,"end_time":15,"choice_num":5},{"voa_id":1020,"title":"Look at them!","title_cn":"看看他们/它们！","text_num":20,"end_time":20,"choice_num":0},{"voa_id":1021,"title":"Which book?","title_cn":"哪一本书？","text_num":12,"end_time":12,"choice_num":5},{"voa_id":1022,"title":"Give me/him/her/us/them a ... Which one?","title_cn":"给我/他/她/我们/他们一\u2026\u2026 哪一\u2026\u2026？","text_num":24,"end_time":24,"choice_num":0},{"voa_id":1023,"title":"Which glasses?","title_cn":"哪几只杯子？","text_num":12,"end_time":12,"choice_num":5},{"voa_id":1024,"title":"Give me/him/her/us/them some ... Which ones?","title_cn":"给我/他/她/我们/他们一些\u2026\u2026 哪些\u2026\u2026？","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1025,"title":"Mrs.smith's kitchen","title_cn":"史密斯太太的厨房","text_num":15,"end_time":15,"choice_num":5},{"voa_id":1026,"title":"Where is it?","title_cn":"它在哪里？","text_num":16,"end_time":16,"choice_num":0},{"voa_id":1027,"title":"Mrs.smith's living room","title_cn":"史密斯太太的客厅","text_num":16,"end_time":16,"choice_num":5},{"voa_id":1028,"title":"Where are they?","title_cn":"它们在哪？","text_num":24,"end_time":24,"choice_num":0},{"voa_id":1029,"title":"Come in, Amy.","title_cn":"进来，艾米。","text_num":12,"end_time":12,"choice_num":5},{"voa_id":1030,"title":"What must I do?","title_cn":"我应该做什么？","text_num":12,"end_time":12,"choice_num":0},{"voa_id":1031,"title":"Where's Sally?","title_cn":"萨利在哪里？","text_num":17,"end_time":17,"choice_num":5},{"voa_id":1032,"title":"What's he/she/it doing?","title_cn":"他/她/它正在做什么？","text_num":32,"end_time":32,"choice_num":0},{"voa_id":1033,"title":"A fine day","title_cn":"晴天","text_num":14,"end_time":14,"choice_num":5},{"voa_id":1034,"title":"What are they doing?","title_cn":"他们在做什么？","text_num":24,"end_time":24,"choice_num":0},{"voa_id":1035,"title":"Our village","title_cn":"我们的村庄","text_num":18,"end_time":18,"choice_num":5},{"voa_id":1036,"title":"Where...?","title_cn":"\u2026\u2026在哪里？","text_num":24,"end_time":24,"choice_num":0},{"voa_id":1037,"title":"Making a bookcase","title_cn":"做书架","text_num":21,"end_time":21,"choice_num":5},{"voa_id":1038,"title":"What are you going to do? What are you doing now?","title_cn":"你准备做什么？你现在正在做什么？","text_num":32,"end_time":32,"choice_num":0},{"voa_id":1039,"title":"Don't drop it!","title_cn":"对不起，先生。","text_num":15,"end_time":15,"choice_num":5},{"voa_id":1040,"title":"What are you going to do? I'm going to...?","title_cn":"你准备做什么？我准备\u2026\u2026","text_num":28,"end_time":28,"choice_num":0},{"voa_id":1041,"title":"Penny's bag","title_cn":"彭妮的提包","text_num":18,"end_time":18,"choice_num":5},{"voa_id":1042,"title":"Is there a ... in /on that ...? Is there any ... in/on that ...?","title_cn":"在那个\u2026\u2026中/上有一个\u2026\u2026吗？在那个\u2026\u2026中/上有\u2026\u2026吗？","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1043,"title":"Hurry up!","title_cn":"快点！","text_num":21,"end_time":21,"choice_num":5},{"voa_id":1044,"title":"Are there any ...? Is there any ...?","title_cn":"有些\u2026\u2026吗？有些\u2026\u2026吗？","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1045,"title":"The boss's letter","title_cn":"老板的信","text_num":21,"end_time":21,"choice_num":5},{"voa_id":1046,"title":"Can you...?","title_cn":"你能\u2026\u2026吗？","text_num":44,"end_time":44,"choice_num":0},{"voa_id":1047,"title":"A cup of coffee","title_cn":"一杯咖啡","text_num":17,"end_time":17,"choice_num":5},{"voa_id":1048,"title":"Do you like...? Do you want ...?","title_cn":"你喜欢\u2026\u2026吗？你想要\u2026\u2026吗？","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1049,"title":"At the butcher's","title_cn":"在肉店","text_num":18,"end_time":18,"choice_num":5},{"voa_id":1050,"title":"He likes... But he doen't like ...","title_cn":"他喜欢\u2026\u2026但是他不喜欢\u2026\u2026","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1051,"title":"A pleasant climate","title_cn":"宜人的气候","text_num":19,"end_time":19,"choice_num":5},{"voa_id":1052,"title":"What nationality are they? Where do they come from?","title_cn":"他们是哪国人？他们来自哪个国家？","text_num":16,"end_time":16,"choice_num":0},{"voa_id":1053,"title":"An interesting climate","title_cn":"有趣的气候","text_num":19,"end_time":19,"choice_num":5},{"voa_id":1054,"title":"What nationality are they? Where do they come from?","title_cn":"他们是哪国人？他们来自哪个国家？","text_num":20,"end_time":20,"choice_num":0},{"voa_id":1055,"title":"The Sawyer family","title_cn":"索耶一家人","text_num":19,"end_time":19,"choice_num":5},{"voa_id":1056,"title":"What do they usually do?","title_cn":"他们通常做什么？","text_num":24,"end_time":24,"choice_num":0},{"voa_id":1057,"title":"An unusual day","title_cn":"很不平常的一天","text_num":19,"end_time":19,"choice_num":5},{"voa_id":1058,"title":"What's the time?","title_cn":"几点钟？","text_num":20,"end_time":20,"choice_num":0},{"voa_id":1059,"title":"Is that all?","title_cn":"就这些吗？","text_num":22,"end_time":22,"choice_num":5},{"voa_id":1060,"title":"What's the time?","title_cn":"几点钟？","text_num":20,"end_time":20,"choice_num":0},{"voa_id":1061,"title":"A bad cold","title_cn":"重感冒","text_num":19,"end_time":19,"choice_num":5},{"voa_id":1062,"title":"What's the matter with them? What must they do?","title_cn":"他们怎么了？他们该怎么办？","text_num":32,"end_time":32,"choice_num":0},{"voa_id":1063,"title":"Thank you, doctor.","title_cn":"谢谢你，医生。","text_num":24,"end_time":24,"choice_num":5},{"voa_id":1064,"title":"Don't...! You mustn't...!","title_cn":"不要\u2026\u2026！你不应该\u2026\u2026！","text_num":18,"end_time":18,"choice_num":0},{"voa_id":1065,"title":"Not a baby","title_cn":"不是一个孩子","text_num":24,"end_time":24,"choice_num":5},{"voa_id":1066,"title":"What's the time?","title_cn":"几点钟？","text_num":24,"end_time":24,"choice_num":0},{"voa_id":1067,"title":"The weekend","title_cn":"周末","text_num":19,"end_time":19,"choice_num":5},{"voa_id":1068,"title":"What's the time?","title_cn":"几点钟？","text_num":20,"end_time":20,"choice_num":0},{"voa_id":1069,"title":"The car race","title_cn":"汽车比赛","text_num":18,"end_time":18,"choice_num":5},{"voa_id":1070,"title":"When were they there?","title_cn":"他们什么时候在那里？","text_num":18,"end_time":18,"choice_num":0},{"voa_id":1071,"title":"He's awful!","title_cn":"他讨厌透了！","text_num":21,"end_time":21,"choice_num":5},{"voa_id":1072,"title":"When did you...?","title_cn":"你什么时候\u2026\u2026？","text_num":20,"end_time":20,"choice_num":0},{"voa_id":1073,"title":"The way to King Street","title_cn":"到国王街的走法","text_num":16,"end_time":16,"choice_num":5},{"voa_id":1074,"title":"What did they do?","title_cn":"他们干了什么？","text_num":22,"end_time":22,"choice_num":0},{"voa_id":1075,"title":"Uncomfortable shoes","title_cn":"不舒适的鞋子","text_num":23,"end_time":23,"choice_num":5},{"voa_id":1076,"title":"When did you...?","title_cn":"你什么时候\u2026\u2026？","text_num":20,"end_time":20,"choice_num":0},{"voa_id":1077,"title":"Terrible toothache","title_cn":"要命的牙痛","text_num":22,"end_time":22,"choice_num":5},{"voa_id":1078,"title":"When did you...?","title_cn":"你什么时候\u2026\u2026？","text_num":40,"end_time":40,"choice_num":0},{"voa_id":1079,"title":"Carol's shopping list","title_cn":"卡罗尔的购物单","text_num":22,"end_time":22,"choice_num":5},{"voa_id":1080,"title":"I must go to the...?","title_cn":"我必须去\u2026\u2026？","text_num":20,"end_time":20,"choice_num":0},{"voa_id":1081,"title":"Roast beef and potatoes","title_cn":"烤牛肉和土豆","text_num":25,"end_time":25,"choice_num":5},{"voa_id":1082,"title":"I had...","title_cn":"我吃（喝）了\u2026\u2026","text_num":24,"end_time":24,"choice_num":0},{"voa_id":1083,"title":"Going on holiday","title_cn":"度假","text_num":26,"end_time":26,"choice_num":5},{"voa_id":1084,"title":"Have you had...?","title_cn":"你已经...？","text_num":29,"end_time":29,"choice_num":0},{"voa_id":1085,"title":"Paris in the spring","title_cn":"巴黎之春","text_num":21,"end_time":21,"choice_num":5},{"voa_id":1086,"title":"What have you done?","title_cn":"你已经做了什么？","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1087,"title":"A car crash","title_cn":"车祸","text_num":22,"end_time":22,"choice_num":5},{"voa_id":1088,"title":"Have you ... yet?","title_cn":"你已经\u2026\u2026了吗？","text_num":32,"end_time":32,"choice_num":0},{"voa_id":1089,"title":"For sale","title_cn":"待售","text_num":24,"end_time":24,"choice_num":5},{"voa_id":1090,"title":"Have you ... yet?","title_cn":"你已经\u2026\u2026了吗？","text_num":32,"end_time":32,"choice_num":0},{"voa_id":1091,"title":"Poor Ian!","title_cn":"可怜的伊恩！","text_num":24,"end_time":24,"choice_num":5},{"voa_id":1092,"title":"When will ...?","title_cn":"什么时候要\u2026\u2026？","text_num":20,"end_time":20,"choice_num":0},{"voa_id":1093,"title":"Our new neighbour","title_cn":"我们的新邻居","text_num":15,"end_time":15,"choice_num":5},{"voa_id":1094,"title":"When did you/will you go to ...?","title_cn":"你过去/将在什么时候去\u2026\u2026？","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1095,"title":"Tickets, please.","title_cn":"请把车票拿出来。","text_num":23,"end_time":23,"choice_num":5},{"voa_id":1096,"title":"What's the exact time?","title_cn":"确切的时间是几点？","text_num":20,"end_time":20,"choice_num":0},{"voa_id":1097,"title":"A small blue case","title_cn":"一只蓝色的小箱子","text_num":22,"end_time":22,"choice_num":5},{"voa_id":1098,"title":"Whose is it? Whose are they?","title_cn":"它是谁的？ 它们是谁的？","text_num":36,"end_time":36,"choice_num":0},{"voa_id":1099,"title":"Ow!","title_cn":"啊哟！","text_num":17,"end_time":17,"choice_num":5},{"voa_id":1100,"title":"He says that... She says that...They say that...","title_cn":"他/她/他们说\u2026\u2026","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1101,"title":"A card from Jimmy","title_cn":"吉米的明信片","text_num":21,"end_time":21,"choice_num":5},{"voa_id":1102,"title":"He says he... She says she...They say they...","title_cn":"他/她/他们说他/她/他们\u2026\u2026","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1103,"title":"The French test","title_cn":"法语考试","text_num":24,"end_time":24,"choice_num":5},{"voa_id":1104,"title":"Too, very, enough","title_cn":"太、非常、足够","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1105,"title":"Full of mistakes","title_cn":"错误百出","text_num":21,"end_time":21,"choice_num":5},{"voa_id":1106,"title":"I want you /him/her/them to... Tell him/her/them to...","title_cn":"我要你/他/她/他们\u2026\u2026 告诉他/她/他们\u2026\u2026","text_num":20,"end_time":20,"choice_num":0},{"voa_id":1107,"title":"It's too small.","title_cn":"太小了。","text_num":20,"end_time":20,"choice_num":5},{"voa_id":1108,"title":"How do they compare?","title_cn":"比一比","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1109,"title":"A good idea","title_cn":"好主意","text_num":26,"end_time":26,"choice_num":5},{"voa_id":1110,"title":"How do they compare?","title_cn":"比一比","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1111,"title":"The most expensive model","title_cn":"最昂贵的型号","text_num":22,"end_time":22,"choice_num":5},{"voa_id":1112,"title":"How do they compare?","title_cn":"比一比","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1113,"title":"Small changes","title_cn":"零钱","text_num":22,"end_time":22,"choice_num":5},{"voa_id":1114,"title":"I've got none.","title_cn":"我没有。","text_num":24,"end_time":24,"choice_num":0},{"voa_id":1115,"title":"Knock, Knock!","title_cn":"敲敲门！","text_num":26,"end_time":26,"choice_num":5},{"voa_id":1116,"title":"Every, no, any and some","title_cn":"每一、无、若干和一些","text_num":20,"end_time":20,"choice_num":0},{"voa_id":1117,"title":"Tommy's breakfast","title_cn":"汤米的早餐","text_num":16,"end_time":16,"choice_num":5},{"voa_id":1118,"title":"What were you doing?","title_cn":"你那时正在做什么？","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1119,"title":"A true story","title_cn":"一个真实的故事","text_num":19,"end_time":19,"choice_num":5},{"voa_id":1120,"title":"It had already happened.","title_cn":"事情已经发生了。","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1121,"title":"The man in a hat","title_cn":"戴帽子的男士","text_num":18,"end_time":18,"choice_num":5},{"voa_id":1122,"title":"Who(whom), which and that","title_cn":"关系代词","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1123,"title":"A Trip to Australia","title_cn":"澳大利亚之行","text_num":23,"end_time":23,"choice_num":5},{"voa_id":1124,"title":"(Who)/(whom), (which) and (that)","title_cn":"关系代词","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1125,"title":"Tea for two","title_cn":"两个人一起喝茶","text_num":22,"end_time":22,"choice_num":5},{"voa_id":1126,"title":"Have to and do not need to","title_cn":"不得不和不必要","text_num":20,"end_time":20,"choice_num":0},{"voa_id":1127,"title":"A famous actress","title_cn":"著名的女演员","text_num":24,"end_time":24,"choice_num":5},{"voa_id":1128,"title":"He can\u2019t be \u2026He must be ...","title_cn":"他不可能\u2026\u2026 他肯定是\u2026\u2026","text_num":28,"end_time":28,"choice_num":0},{"voa_id":1129,"title":"Seventy miles an hour","title_cn":"时速70英里","text_num":24,"end_time":24,"choice_num":5},{"voa_id":1130,"title":"He can't have been ... He must have been ...","title_cn":"他那时不可能\u2026\u2026 他那时肯定是\u2026\u2026","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1131,"title":"Don't be so sure!","title_cn":"别那么肯定！","text_num":23,"end_time":23,"choice_num":5},{"voa_id":1132,"title":"He may be ... He may have been ...I\u2019m not sure.","title_cn":"他可能是\u2026\u2026 他可能已经\u2026\u2026 我不敢肯定。","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1133,"title":"Sensational news!","title_cn":"爆炸性新闻！","text_num":19,"end_time":19,"choice_num":5},{"voa_id":1134,"title":"He said (that) he ... He told me (that) he ...","title_cn":"他曾说他\u2026\u2026 他曾告诉我说他\u2026\u2026","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1135,"title":"The latest report","title_cn":"最新消息","text_num":23,"end_time":23,"choice_num":5},{"voa_id":1136,"title":"He said (that) he ... He told me (that) he ...","title_cn":"他（曾）说他\u2026\u2026 他（曾）告诉我说他 \u2026\u2026","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1137,"title":"A pleasant dream","title_cn":"美好的梦","text_num":19,"end_time":19,"choice_num":5},{"voa_id":1138,"title":"If...","title_cn":"如果\u2026\u2026","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1139,"title":"Is that you,John?","title_cn":"是你吗，约翰？","text_num":22,"end_time":22,"choice_num":5},{"voa_id":1140,"title":"He wants to know if/why/what/when","title_cn":"他想知道是否/为什么/什么/什么时候","text_num":30,"end_time":30,"choice_num":0},{"voa_id":1141,"title":"Sally's first train ride","title_cn":"萨莉第一交乘火车旅行","text_num":18,"end_time":18,"choice_num":5},{"voa_id":1142,"title":"Someone invited Sally to a party. Sally was invited to a party.","title_cn":"有人邀请萨莉出席一个聚会。萨莉应邀出席一个聚会。","text_num":20,"end_time":20,"choice_num":0},{"voa_id":1143,"title":"A walk through the woods","title_cn":"林中散步","text_num":16,"end_time":16,"choice_num":5},{"voa_id":1144,"title":"He hasn\u2019t been served yet. He will be served soon.","title_cn":"还没有人来侍候他。很快会有人来侍候他的。","text_num":20,"end_time":20,"choice_num":0}]
     */

    private int size;
    private List<DataBean> data;

    public static UnitTitle objectFromData(String str) {

        return new Gson().fromJson(str, UnitTitle.class);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean{

        /**
         * listenPercentage : 0
         * text_num : 8
         * totalTime : 1479
         * titleid : 12753,12754
         * end_time : 20.4
         * packageid : 825
         * pic : http://static2.iyuba.cn/newconcept/illustration/1/1.jpg
         * title : Excuse me!
         * ownerid : 21
         * price : 990
         * voa_id : 1001
         * percentage : 0
         * title_cn : 对不起！
         * choice_num : 5
         * name : 新概念第一册(1-72课)手绘影视轻松学
         * wordNum : 8
         * categoryid : 1126
         * desc : 激活记忆新方法：手绘漫画，情境视听，轻松愉快学英语！
         */

        private int voa_id;
        private String title;
        private String title_cn;
        private int text_num;
        private double end_time;
        private int choice_num;

        private int categoryid;
        private int packageid;
        private int ownerid;
        private int totalTime;
        private String titleid;
        private String name;
        private String price;
        private String desc;

        private String listenPercentage;
        private String pic;
        private String percentage;
        private String wordNum;

        public int getVoa_id() {
            return voa_id;
        }

        public void setVoa_id(int voa_id) {
            this.voa_id = voa_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle_cn() {
            return title_cn;
        }

        public void setTitle_cn(String title_cn) {
            this.title_cn = title_cn;
        }

        public int getText_num() {
            return text_num;
        }

        public void setText_num(int text_num) {
            this.text_num = text_num;
        }

        public double getEnd_time() {
            return end_time;
        }

        public void setEnd_time(double end_time) {
            this.end_time = end_time;
        }

        public int getChoice_num() {
            return choice_num;
        }

        public void setChoice_num(int choice_num) {
            this.choice_num = choice_num;
        }

        public int getCategoryid() {
            return categoryid;
        }

        public void setCategoryid(int categoryid) {
            this.categoryid = categoryid;
        }

        public int getPackageid() {
            return packageid;
        }

        public void setPackageid(int packageid) {
            this.packageid = packageid;
        }

        public int getOwnerid() {
            return ownerid;
        }

        public void setOwnerid(int ownerid) {
            this.ownerid = ownerid;
        }

        public int getTotalTime() {
            return totalTime;
        }

        public void setTotalTime(int totalTime) {
            this.totalTime = totalTime;
        }

        public String getTitleid() {
            return titleid;
        }

        public void setTitleid(String titleid) {
            this.titleid = titleid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getListenPercentage() {
            return listenPercentage;
        }

        public void setListenPercentage(String listenPercentage) {
            this.listenPercentage = listenPercentage;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public String getPercentage() {
            return percentage;
        }

        public void setPercentage(String percentage) {
            this.percentage = percentage;
        }

        public String getWordNum() {
            return wordNum;
        }

        public void setWordNum(String wordNum) {
            this.wordNum = wordNum;
        }
    }
}
