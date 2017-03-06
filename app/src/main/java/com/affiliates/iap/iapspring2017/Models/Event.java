//
//  Event.java
//  IAP
//
//  Created by Gabriel S. Santiago on 3/6/17.
//  Copyright © 2017 IAP Conference UPRM. All rights reserved.
//

package com.affiliates.iap.iapspring2017.Models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Event {
   private Date startDate;
   private Date enDate;
   private String eventName;
   private String eventType;
   private String eventDescription;

   private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
