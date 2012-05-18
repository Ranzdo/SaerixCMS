SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `cms_controllers`
-- ----------------------------
CREATE TABLE `cms_controllers` (
  `host_id` int(11) NOT NULL,
  `controller_name` varchar(100) NOT NULL,
  `controller_content` text NOT NULL,
  PRIMARY KEY (`host_id`,`controller_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of cms_controllers
-- ----------------------------

-- ----------------------------
-- Table structure for `cms_databases`
-- ----------------------------
CREATE TABLE `cms_databases` (
  `database_id` int(1) NOT NULL,
  `database_name` varchar(100) NOT NULL,
  `database_url` varchar(100) DEFAULT NULL,
  `database_username` varchar(100) DEFAULT NULL,
  `database_password` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`database_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of cms_databases
-- ----------------------------

-- ----------------------------
-- Table structure for `cms_hosts`
-- ----------------------------
CREATE TABLE `cms_hosts` (
  `host_id` int(11) NOT NULL AUTO_INCREMENT,
  `host_value` varchar(100) NOT NULL,
  PRIMARY KEY (`host_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of cms_hosts
-- ----------------------------

-- ----------------------------
-- Table structure for `cms_models`
-- ----------------------------
CREATE TABLE `cms_models` (
  `database_id` int(11) NOT NULL,
  `model_tablename` varchar(100) NOT NULL,
  `model_content` text NOT NULL,
  PRIMARY KEY (`database_id`,`model_tablename`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of cms_models
-- ----------------------------

-- ----------------------------
-- Table structure for `cms_routes`
-- ----------------------------
CREATE TABLE `cms_routes` (
  `route_id` int(11) NOT NULL AUTO_INCREMENT,
  `host_id` int(11) NOT NULL,
  `route_type` int(11) NOT NULL,
  `route_suffix` varchar(100) NOT NULL,
  `route_value` varchar(100) NOT NULL,
  PRIMARY KEY (`route_id`),
  UNIQUE KEY `route_suffix` (`route_suffix`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of cms_routes
-- ----------------------------

-- ----------------------------
-- Table structure for `cms_users`
-- ----------------------------
CREATE TABLE `cms_users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `unqiueusername` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of cms_users
-- ----------------------------

-- ----------------------------
-- Table structure for `cms_views`
-- ----------------------------
CREATE TABLE `cms_views` (
  `host_id` int(11) NOT NULL,
  `view_name` varchar(100) NOT NULL,
  `view_content` text NOT NULL,
  PRIMARY KEY (`host_id`,`view_name`),
  UNIQUE KEY `u` (`view_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of cms_views
-- ----------------------------
