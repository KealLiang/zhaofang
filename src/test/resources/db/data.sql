INSERT INTO `user`(`id`, `name`, `email`, `phone_number`, `password`, `status`, `last_login_time`) VALUES (1, 'wali',
'wali@imooc.com', '13888888888', 'wali', 1, NOW());
INSERT INTO `user`(`id`, `name`, `email`, `phone_number`, `password`, `status`, `last_login_time`) VALUES (2, 'admin',
'admin@imooc.com', '13999999999', '55b3d0936a3fb63168d57a6bda0ddbbf', 1, NOW());

INSERT INTO `role`(`id`, `user_id`, `name`) VALUES (1, 1, 'USER');
INSERT INTO `role`(`id`, `user_id`, `name`) VALUES (2, 2, 'USER');
INSERT INTO `role`(`id`, `user_id`, `name`) VALUES (3, 3, 'ADMIN');

-- address data
INSERT INTO `support_address`(`id`, `belong_to`, `en_name`, `cn_name`, `level`, `baidu_map_lng`, `baidu_map_lat`)
  VALUES ('4', 'bj', 'bj', '北京', 'city', '116.395645','39.929986'),
      ('5', 'sh', 'sh', '上海', 'city', '121.487899', '31.249162'),
      ('6', 'hb', 'sjz', '石家庄', 'city', '114.522082', '38.048958'),
      ('7', 'hb', 'ts', '唐山', 'city', '118.183451', '39.650531'),
      ('8', 'hb', 'hd', '邯郸', 'city', '114.482694', '36.609308'),
      ('9', 'bj', 'dcq', '东城区', 'region', '116.42188470126446', '39.93857401298612'),
      ('10', 'bj', 'xcq', '西城区', 'region', '116.37319010401802', '39.93428014370851'),
      ('12', 'bj', 'hdq', '海淀区', 'region', '116.23967780102151', '40.03316204507791'),
      ('13', 'bj', 'cpq', '昌平区', 'region', '116.21645635689414', '40.2217235498323'),
      ('14', 'sh', 'ptq', '普陀区', 'region', '121.39844294374956', '31.263742929075534'),
      ('15', 'sjz', 'caq', '长安区', 'region', '114.59262155387033', '38.07687479578663'),
      ('16', 'sjz', 'qdq', '桥东区', 'region', '114.51078430496142', '38.06338975380927'),
      ('17', 'sjz', 'qxq', '桥西区', 'region', '114.43813995531943', '38.033364550068136'),
      ('18', 'sjz', 'xhq', '新华区', 'region', '114.4535014286928', '38.117218640478164'),
      ('19', 'bj', 'cyq', '朝阳区', 'region', '116.52169489108084', '39.95895316640668');

-- house data
INSERT INTO `house`(`id`, `title`, `price`, `area`, `room`, `floor`, `total_floor`, `watch_times`, `build_year`,
                    `status`, `create_time`, `last_update_time`, `city_en_name`, `region_en_name`, `cover`,
                    `direction`, `distance_to_subway`, `parlour`, `district`, `admin_id`, `bathroom`, `street`)
  VALUES ('15', '富力城 国贸CBD 时尚休闲 商务办公 超棒瓦力', '6200', '70', '2', '10', '20', '2', '2005', '1', '2017-09-06 18:56:14', '2017-09-15 00:26:59', 'bj', 'hdq', 'FmD3zKG-gUPOrNv0HwzZN7IbkAC_', '2', '10', '1', '融泽嘉园', '2', '0', '龙域西二路'),
  ('16', '富力城 国贸CBD 时尚休闲 商务办公', '6300', '70', '2', '10', '20', '0', '2012', '1', '2017-09-06 19:53:35', '2017-09-11 00:35:13', 'bj', 'hdq', 'FmD3zKG-gUPOrNv0HwzZN7IbkAC_', '1', '-1', '1', '融泽嘉园', '2', '0', '龙域西二路'),
  ('17', '二环东直门地铁站附近、王府井、天安门、国贸、三里屯、南锣鼓巷', '3000', '35', '1', '5', '10', '0', '2013', '1', '2017-09-06 20:45:35', '2017-09-11 00:35:05', 'bj', 'hdq', 'FhVdDhzVDH1dLVVx4jOaVXOCfnea', '1', '200', '0', '融泽嘉园', '2', '0', '龙域西二路'),
  ('18', '华贸城 东向一居挑空loft 干净温馨 随时可以签约', '5700', '52', '1', '7', '20', '0', '2012', '1', '2017-09-06 21:01:02', '2017-09-11 00:35:01', 'bj', 'hdq', 'FsxiS6rOTpSg5pK7tv41e8Zpnn_c', '2', '1085', '1', '融泽嘉园', '2', '0', '龙域西二路'),
  ('19', '望春园板楼三居室 自住精装 南北通透 采光好视野棒！', '9200', '132', '3', '6', '14', '0', '2005', '1', '2017-09-06 22:44:25', '2017-09-11 00:34:55', 'bj', 'hdq', 'Fl1lNikhmMIecbTn-JTsurxugtFU', '2', '1108', '2', '融泽嘉园', '2', '0', '龙域西二路'),
  ('20', '哈哈 高大上的整租两居室 业主诚意出租', '5400', '56', '2', '12', '20', '0', '2012', '1', '2017-09-06 23:39:50', '2017-09-11 00:34:21', 'bj', 'hdq', 'FtNl9uPM6p5PjEs8z2FnOuViNtOM', '2', '-1', '1', '融泽嘉园', '2', '0', '龙域西二路'),
  ('21', '新康园 正规三居室 精装修 家电家具齐全', '1900', '18', '1', '13', '25', '0', '2012', '1', '2017-09-07 00:52:47', '2017-09-11 00:34:13', 'bj', 'hdq', 'Fn9sHC3Wx7qpYCmSxt-z8FZluf0Z', '3', '1302', '0', '融泽嘉园', '2', '0', '龙域西二路'),
  ('24', '湖光壹号望京华府183-387㎡阔景大宅', '50000', '288', '5', '1', '1', '0', '2015', '1', '2017-09-07 11:42:20', '2017-09-18 20:14:14', 'bj', 'hdq', 'FvVqU8LneZZ5xaLBAOM1KXR2Pz1X', '5', '200', '3', '融泽嘉园', '2', '0', '龙域西二路');

-- house_detail data
INSERT INTO `house_detail` VALUES ('21', '国贸CBD商务区,近SOHO现代城,富顿中心,富力城商业街区,乐成中心,潘家园古玩城,八王坟长途客运站,北京游乐园,经由三环路可直达首都机场。附近有双井桥南,双井桥北,双井桥东双井桥西等30多条公交站牌!\n《天安门,故宫,王府井,三里屯,前门,天坛,北海,颐和园,雍和宫,奥林匹克公园,水立方,西单,欢乐谷,燕莎商城等》知名购物区及旅游名胜古迹,是您休闲旅游及商务下榻的理想选择', '房间采光良好,落地窗外景色宜人', '房子处于北京的CBD商务中心区国贸双井!紧邻双井地铁站,步行5分钟即到!这离国贸、中央电视台、潘家园、三里屯、团结湖、日坛使馆区、儿研所、大郊亭都很近', '房子闹中取静,地理位置优越,交通方便,紧邻呼家楼地铁站和东大桥地铁站;去机场可乘坐东直门机场快轨,非常方便｡购物中心有双井购物中心、国贸购物中心和侨福芳草地购物中心、三里屯购物中心等,远道而来的朋友可尽览都市璀璨!', '0', '二号院7号楼', '4', '10号线', '58', '双井', '15'), ('22', '国贸CBD商务区,近SOHO现代城,富顿中心,富力城商业街区,乐成中心,潘家园古玩城,八王坟长途客运站,北京游乐园,经由三环路可直达首都机场。附近有双井桥南,双井桥北,双井桥东双井桥西等30多条公交站牌!\n《天安门,故宫,王府井,三里屯,前门,天坛,北海,颐和园,雍和宫,奥林匹克公园,水立方,西单,欢乐谷,燕莎商城等》知名购物区及旅游名胜古迹,是您休闲旅游及商务下榻的理想选择!', '房间采光良好,落地窗外景色宜人', '房子处于北京的CBD商务中心区国贸双井!紧邻双井地铁站,步行5分钟即到', '这离国贸、中央电视台、潘家园、三里屯、团结湖、日坛使馆区、儿研所、大郊亭都很近。房子闹中取静,地理位置优越,交通方便,紧邻呼家楼地铁站和东大桥地铁站;去机场可乘坐东直门机场快轨,非常方便｡购物中心有双井购物中心、国贸购物中心和侨福芳草地购物中心、三里屯购物中心等,远道而来的朋友可尽览都市璀璨！', '0', '1号院1号楼', null, null, null, null, '16'), ('24', '我和我女盆友当房东已经一年了,也是超赞房东,希望能为大家提供舒适的住所~ 房间的大门和房门都是密码门,小区有保安24小时值班,非常安全方便。 通常入住时间是下午三点,提前来的同学可以先寄存行李和洗澡哦~\n\n', '房間非常漂亮,空間很大,鵝黃色的牆壁看起來非常舒服', '位置距離地鐵站不遠', '距故宫、天安门、王府井、三里屯、簋街、南锣鼓巷等景点均可地铁半小时内到达,交通便利~', '0', '1号院2号楼', '1', '13号线', '16', '东直门', '17'), ('25', '这个经纪人很懒，没写核心卖点', '此房是一居室的格局，上下两层，面宽，房间亮堂，进门右手厨房，正前方是25平米的客厅，楼上是卧室，带洗手间！ 喧闹和安静隔开，适合居住', '小区距离地铁13号线北苑站500米的距离，交通出行便利....', '小区楼下就是华贸天地娱乐街，保利电影院，眉州东坡，中信银行，麦当劳等娱乐休闲设施齐全', '0', '1号院3号楼', '1', '13号线', '11', '北苑', '18'), ('26', '这个经纪人很懒，没写核心卖点', '此房为望春园小区板楼南北通透户型，主卧客厅朝南，次卧朝北，两个客厅双卫，居住很舒适。', '距离地铁5号线立水桥南站630米，有464,465,966,081，621等多条公交线路，交通出行四通八达。', '小区旁有大型购物商场易事达，物美超市，丰宁蔬菜基地，航空总医院、安贞医院北苑分院，中国银行、中国农业银行、中国工商银行、中国交通银行、中国建设银行、招商银行分布。小区旁有天奥健身房，还有立水桥公园..', '0', '6号院1号楼', '1', '13号线', '10', '立水桥', '19'), ('27', '高大上的整租两居室 业主诚意出租\n1、客厅挑高、宽敞舒适、阳光充足 2、卧室搭配的很新颖，使用之高 3、厨房带阳台，让您和家人有足够的空间展现私家厨艺', '客厅挑高、宽敞舒适、阳光充足 2、卧室搭配的很新颖，使用之高 3、厨房带阳台，让您和家人有足够的空间展现私家厨艺', '近地铁13号线东直门站', '社区环境好，环境优美，适宜居住，人文素质高，物业管理完善； 2、属于低密度社区 ，适宜居住 3、小区的林密树多，让您感受花园一样的家', '0', '1号院5号楼', '1', '13号线', '16', '东直门', '20'), ('28', '房子是正规三室一厅一厨一卫，装修保持的不错，家电家具都齐全。\n', '房子客厅朝北面积比较大，主卧西南朝向，次卧朝北，另一个次卧朝西，两个次卧面积差不多大。', '小区出南门到8号线育新地铁站614米，交通便利，小区500米范围内有物美，三旗百汇，龙旗广场等几个比较大的商场，生活购物便利，出小区北门朝东952米是地铁霍营站，是8号线和 13号线的换乘站，同时还有个S2线，通往怀来。（数据来源百度地图）', '小区西边300米就是物美超市和三旗百汇市场（日常百货、粮油米面、瓜果蔬菜、生鲜海货等等，日常生活很便利，消费成本低），北边200米是龙旗购物广场和永辉超市（保利影院，KFC，麦当劳等，轻松满足娱乐消费）。小区里还有商店，饭店，家政等。', '0', '2号院1号楼', '1', '13号线', '9', '霍营', '21'), ('31', '懒死了 不谢', '户型介绍', '交通出行', '周边配套', '0', '3号院1号楼', '1', '13号线', '12', '望京西', '24'), ('32', '房屋描述-编辑', '户型介绍', '交通出行', '周边配套-编辑', '0', '3号院2单元1003', '1', '13号线', '8', '回龙观', '25');

-- house tag data
INSERT INTO `house_tag` VALUES ('15', '18', '独立阳台'), ('15', '17', '空调'), ('16', '16', '光照充足'), ('17', '15', '随时看房'), ('17', '14', '集体供暖'), ('18', '13', '精装修'), ('19', '12', '独立卫生间'), ('19', '11', '独立阳台'), ('21', '19', '光照充足'), ('21', '20', '独立卫生间'), ('24', '10', '光照充足'), ('24', '3', '精装修'), ('24', '8', '集体供暖'), ('25', '21', '独立阳台');