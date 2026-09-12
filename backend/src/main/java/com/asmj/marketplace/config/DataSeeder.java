package com.asmj.marketplace.config;

import com.asmj.marketplace.application.model.JobApplication;
import com.asmj.marketplace.application.repository.JobApplicationRepository;
import com.asmj.marketplace.booking.model.Booking;
import com.asmj.marketplace.booking.repository.BookingRepository;
import com.asmj.marketplace.category.model.Category;
import com.asmj.marketplace.category.repository.CategoryRepository;
import com.asmj.marketplace.chat.model.Conversation;
import com.asmj.marketplace.chat.model.Message;
import com.asmj.marketplace.chat.repository.ConversationRepository;
import com.asmj.marketplace.chat.repository.MessageRepository;
import com.asmj.marketplace.common.model.StatusHistory;
import com.asmj.marketplace.post.model.Post;
import com.asmj.marketplace.post.repository.PostRepository;
import com.asmj.marketplace.user.model.User;
import com.asmj.marketplace.user.repository.UserRepository;
import com.asmj.marketplace.vendor.model.Vendor;
import com.asmj.marketplace.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.*;

@Component @RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users;
    private final CategoryRepository cats;
    private final VendorRepository vendors;
    private final PostRepository posts;
    private final BookingRepository bookings;
    private final JobApplicationRepository applications;
    private final ConversationRepository conversations;
    private final MessageRepository messages;
    private final PasswordEncoder encoder;
    @Value("${app.seed.enabled:true}") boolean enabled;

    @Override public void run(String... args) {
        if (!enabled) return;
        Instant now = Instant.now();
        User admin = ensureUser("ASMJ Admin","admin@asmj.co.in","9999900000",Set.of(User.Role.ADMIN));
        Map<String,Category> categories = seedCategories(now);
        Map<String,Vendor> vs = seedVendors(now);
        Map<String,User> customers = seedCustomers(now);
        Map<String,Post> demoPosts = seedPosts(categories,vs,now);
        seedTransactions(demoPosts,vs,customers,now);
        seedChats(vs,customers,now);
    }

    private User ensureUser(String name,String email,String mobile,Set<User.Role> roles){
        User user=users.findByEmailIgnoreCase(email).orElse(null);
        if(user==null){
            user=User.builder().name(name).email(email).mobile(mobile)
                .password(encoder.encode("Demo@12345")).roles(new HashSet<>(roles)).status(User.Status.ACTIVE)
                .createdAt(Instant.now()).updatedAt(Instant.now()).build();
        }else{
            if(user.getRoles()==null) user.setRoles(new HashSet<>());
            user.getRoles().addAll(roles);
            if(user.getStatus()==null) user.setStatus(User.Status.ACTIVE);
            if(user.getName()==null||user.getName().isBlank()) user.setName(name);
            if(user.getMobile()==null||user.getMobile().isBlank()) user.setMobile(mobile);
            user.setUpdatedAt(Instant.now());
        }
        return users.save(user);
    }

    private Map<String,Category> seedCategories(Instant now){
        String[][] data={{"Products","products"},{"Services","services"},{"Events","events"},{"Insurance","insurance"},{"Jobs","jobs"},{"Tutors","tutors"},{"Rentals","rentals"}};
        Map<String,Category> out=new LinkedHashMap<>();
        for(String[] x:data){
            Category c=cats.findBySlug(x[1]).orElseGet(() -> cats.save(Category.builder().name(x[0]).slug(x[1]).description("ASMJ marketplace "+x[0].toLowerCase()).active(true).attributes(new ArrayList<>()).createdAt(now).build()));
            out.put(x[1],c);
        }
        return out;
    }

    private Map<String,Vendor> seedVendors(Instant now){
        String[][] data={
            {"demo.products@asmj.co.in","Demo Products Hub","Retail & Products","Quality everyday products and office supplies."},
            {"demo.services@asmj.co.in","Demo Services Studio","Professional Services","Home, business and creative services."},
            {"demo.learning@asmj.co.in","Demo Learning Center","Education & Tutors","Tutoring, coaching and skill development."},
            {"demo.rentals@asmj.co.in","Demo Rentals & Properties","Rentals & Property","Property and vehicle rental options."}
        };
        Map<String,Vendor> out=new LinkedHashMap<>();
        for(String[] x:data){
            User u=ensureUser(x[1],x[0],"98888"+String.valueOf(out.size()+1)+"001",Set.of(User.Role.VENDOR));
            Vendor v=vendors.findByUserId(u.getId()).orElseGet(() -> vendors.save(Vendor.builder().userId(u.getId()).businessName(x[1]).businessType(x[2]).description(x[3])
                .phone(u.getMobile()).email(u.getEmail()).address(Map.of("city","Bengaluru","state","Karnataka","country","India","pincode","560001"))
                .documents(List.of("https://placehold.co/1000x1400/png?text=Business+Document")).verificationStatus(Vendor.VerificationStatus.APPROVED)
                .status(Vendor.Status.ACTIVE).createdAt(now).updatedAt(now).build()));
            out.put(x[0],v);
        }
        // A separate pending vendor makes the Admin Approval Center useful for testing.
        User pu=ensureUser("Pending Demo Vendor","pending.vendor@asmj.co.in","9777700005",Set.of(User.Role.VENDOR));
        vendors.findByUserId(pu.getId()).orElseGet(() -> vendors.save(Vendor.builder().userId(pu.getId()).businessName("Pending Demo Business")
            .businessType("Insurance Agency").description("Demo vendor awaiting administrator verification.").phone(pu.getMobile()).email(pu.getEmail())
            .address(Map.of("city","Bengaluru","state","Karnataka","country","India")).documents(List.of("https://placehold.co/1000x1400/png?text=KYC+Document"))
            .verificationStatus(Vendor.VerificationStatus.PENDING).status(Vendor.Status.ACTIVE).createdAt(now).updatedAt(now).build()));
        return out;
    }

    private Map<String,User> seedCustomers(Instant now){
        Map<String,User> out=new LinkedHashMap<>();
        out.put("customer1",ensureUser("Aarav Demo","demo.user@asmj.co.in","9000000001",Set.of(User.Role.USER)));
        out.put("customer2",ensureUser("Diya Demo","demo.customer2@asmj.co.in","9000000002",Set.of(User.Role.USER)));
        out.put("customer3",ensureUser("Kabir Demo","demo.customer3@asmj.co.in","9000000003",Set.of(User.Role.USER)));
        return out;
    }

    private Map<String,Post> seedPosts(Map<String,Category> cats,Map<String,Vendor> vs,Instant now){
        Map<String,Post> out=new LinkedHashMap<>();
        if(posts.findAll().stream().anyMatch(p->p.getTitle()!=null&&p.getTitle().startsWith("Demo · "))){
            posts.findAll().stream().filter(p->p.getTitle()!=null&&p.getTitle().startsWith("Demo · ")).forEach(p->out.put(p.getTitle(),p));
            return out;
        }
        List<SeedPost> data=List.of(
            new SeedPost("products","demo.products@asmj.co.in","PRODUCT","Demo · Wireless Keyboard","Compact wireless keyboard with silent keys and USB receiver.",1299.0,"per item",Map.of("brand","ASMJ Demo","condition","New","warranty","1 year")),
            new SeedPost("products","demo.products@asmj.co.in","PRODUCT","Demo · Office Pen Set","Premium blue and black pens for school and office use.",249.0,"per pack",Map.of("packSize",10,"ink","Blue/Black")),
            new SeedPost("products","demo.products@asmj.co.in","PRODUCT","Demo · Desk Organizer","Multi-compartment desk organizer for a clean workspace.",599.0,"per item",Map.of("material","ABS","color","Black")),
            new SeedPost("services","demo.services@asmj.co.in","SERVICE","Demo · Home Deep Cleaning","Professional deep cleaning for apartments and independent homes.",2499.0,"starting from",Map.of("duration","4 hours","teamSize",3,"serviceArea","Bengaluru")),
            new SeedPost("services","demo.services@asmj.co.in","SERVICE","Demo · AC Service","Inspection, cleaning and basic servicing for split AC units.",699.0,"per unit",Map.of("serviceType","Split AC","visitCharge",199,"warrantyDays",30)),
            new SeedPost("services","demo.services@asmj.co.in","SERVICE","Demo · Logo Design","Business logo concepts with source files and brand-ready exports.",3500.0,"starting from",Map.of("revisions",3,"deliveryDays",5,"formats","PNG, SVG, PDF")),
            new SeedPost("events","demo.services@asmj.co.in","EVENT","Demo · Bengaluru Business Meetup","Networking evening for founders, freelancers and local businesses.",499.0,"per attendee",Map.of("eventDate","2026-10-10","capacity",120,"venue","Bengaluru")),
            new SeedPost("events","demo.services@asmj.co.in","EVENT","Demo · Kids Art Workshop","Weekend guided art workshop for children aged 7–14.",799.0,"per attendee",Map.of("eventDate","2026-10-18","duration","3 hours","ageGroup","7-14")),
            new SeedPost("events","demo.services@asmj.co.in","EVENT","Demo · Career Networking Fair","Meet employers, trainers and recruiters in one place.",0.0,"free",Map.of("eventDate","2026-11-07","booths",40,"venue","Bengaluru")),
            new SeedPost("insurance","demo.services@asmj.co.in","INSURANCE","Demo · Family Health Insurance","Health insurance plan enquiry for families with flexible coverage options.",18000.0,"annual estimate",Map.of("coverage","10 lakh","members","2 adults + 2 children","ageRange","25-45")),
            new SeedPost("insurance","demo.services@asmj.co.in","INSURANCE","Demo · Two Wheeler Insurance","Motor insurance quote assistance for private two-wheelers.",2200.0,"annual estimate",Map.of("vehicleType","Two Wheeler","coverage","Comprehensive","claimSupport","Yes")),
            new SeedPost("insurance","demo.services@asmj.co.in","INSURANCE","Demo · Shop Insurance","Protection options for small retail shops and inventory.",8500.0,"annual estimate",Map.of("propertyType","Retail Shop","sumInsured","25 lakh","location","Bengaluru")),
            new SeedPost("jobs","demo.services@asmj.co.in","JOB","Demo · Java Spring Boot Developer","Build REST APIs and MongoDB integrations for marketplace products.",900000.0,"annual salary",Map.of("experience","2-5 years","workMode","Hybrid","skills","Java, Spring Boot, MongoDB")),
            new SeedPost("jobs","demo.services@asmj.co.in","JOB","Demo · Sales Executive","Grow local merchant partnerships and manage inbound leads.",480000.0,"annual salary",Map.of("experience","1-3 years","workMode","On-site","incentives","Performance based")),
            new SeedPost("jobs","demo.services@asmj.co.in","JOB","Demo · UI/UX Designer","Design responsive marketplace journeys and reusable components.",750000.0,"annual salary",Map.of("experience","2-4 years","workMode","Remote","tools","Figma, FigJam")),
            new SeedPost("tutors","demo.learning@asmj.co.in","TUTOR","Demo · Mathematics Tutor","One-to-one mathematics tutoring for school students.",700.0,"per class",Map.of("subjects","Mathematics","classes","6-10","mode","Online + Offline")),
            new SeedPost("tutors","demo.learning@asmj.co.in","TUTOR","Demo · Spoken English Coach","Practical English sessions for students and working professionals.",500.0,"per class",Map.of("level","Beginner to Advanced","mode","Online","duration","60 minutes")),
            new SeedPost("tutors","demo.learning@asmj.co.in","TUTOR","Demo · Coding Tutor","Programming fundamentals and project mentoring for beginners.",900.0,"per class",Map.of("subjects","Java, Python, Web","mode","Online","duration","75 minutes")),
            new SeedPost("rentals","demo.rentals@asmj.co.in","PROPERTY","Demo · 2BHK Apartment for Rent","Bright 2BHK apartment near metro and daily conveniences.",28000.0,"per month",Map.of("bedrooms",2,"bathrooms",2,"furnishing","Semi Furnished","areaSqFt",1180)),
            new SeedPost("rentals","demo.rentals@asmj.co.in","PROPERTY","Demo · Commercial Office Space","Ready-to-use office space suitable for startups and small teams.",65000.0,"per month",Map.of("areaSqFt",1800,"parking","4 cars","furnishing","Fully Furnished")),
            new SeedPost("rentals","demo.rentals@asmj.co.in","VEHICLE_RENTAL","Demo · Sedan Rental","Comfortable sedan for city travel and weekend trips.",2200.0,"per day",Map.of("vehicle","Sedan","seats",5,"fuel","Petrol","securityDeposit",5000)),
            new SeedPost("rentals","demo.rentals@asmj.co.in","VEHICLE_RENTAL","Demo · SUV Rental","Spacious SUV rental for family and outstation travel.",3800.0,"per day",Map.of("vehicle","SUV","seats",7,"fuel","Diesel","securityDeposit",8000))
        );
        for(SeedPost d:data){
            Vendor v=vs.get(d.vendorEmail()); Category c=cats.get(d.slug());
            Post p=Post.builder().vendorId(v.getId()).categoryId(c.getId()).type(d.type()).title(d.title()).slug(slug(d.title()))
                .description(d.description()).price(d.price()).priceType(d.priceType()).images(List.of(image(d.title(),1),image(d.title(),2)))
                .attachments(List.of(Post.MediaItem.builder().secureUrl("https://placehold.co/1200x1600/png?text=Demo+Attachment").fileName("demo-details.pdf").mediaType("application/pdf").resourceType("image").purpose("listing").build()))
                .location(Post.Location.builder().country("India").state("Karnataka").city("Bengaluru").area("Indiranagar").pincode("560038").build())
                .attributes(new HashMap<>(d.attributes())).status(Post.PostStatus.PUBLISHED).approvalStatus(Post.PostStatus.APPROVED).views((long)(10+out.size()*7)).featured(out.size()%5==0).createdAt(now.minusSeconds(out.size()*3600L)).updatedAt(now).build();
            Post saved=posts.save(p); out.put(d.title(),saved);
        }
        // Two pending examples so the admin approval center always has realistic test records.
        Vendor pv=vendors.findByUserId(users.findByEmailIgnoreCase("pending.vendor@asmj.co.in").orElseThrow().getId()).orElseThrow();
        for(int i=1;i<=2;i++){
            String title="Demo · Pending Listing "+i;
            Post p=Post.builder().vendorId(pv.getId()).categoryId(cats.get("products").getId()).type("PRODUCT").title(title).slug(slug(title))
                .description("Demo listing waiting for administrator review.").price(999.0+i*100).priceType("per item").images(List.of(image(title,1)))
                .attachments(List.of(Post.MediaItem.builder().secureUrl("https://placehold.co/1200x1600/png?text=Pending+Document").fileName("pending-document.pdf").mediaType("application/pdf").resourceType("image").purpose("listing").build()))
                .location(Post.Location.builder().country("India").state("Karnataka").city("Bengaluru").area("Whitefield").pincode("560066").build())
                .attributes(Map.of("condition","New","demo","Pending approval")).status(Post.PostStatus.PENDING_APPROVAL).approvalStatus(Post.PostStatus.PENDING_APPROVAL).createdAt(now).updatedAt(now).build();
            posts.save(p);
        }
        return out;
    }

    private void seedTransactions(Map<String,Post> ps,Map<String,Vendor> vs,Map<String,User> cs,Instant now){
        User u=cs.get("customer1");
        Post product=find(ps,"Demo · Wireless Keyboard"), service=find(ps,"Demo · Home Deep Cleaning"), event=find(ps,"Demo · Bengaluru Business Meetup"), tutor=find(ps,"Demo · Mathematics Tutor"), rental=find(ps,"Demo · Sedan Rental"), property=find(ps,"Demo · 2BHK Apartment for Rent"), job=find(ps,"Demo · Java Spring Boot Developer");
        if(bookings.findByUserIdOrderByCreatedAtDesc(u.getId()).isEmpty()){
            bookings.save(booking("ORDER",product,u,vs,2,2598.0,"DELIVERED",now.minus(Duration.ofDays(4)),Map.of("deliveryAddress","Indiranagar, Bengaluru"),now));
            bookings.save(booking("BOOKING",service,u,vs,1,2499.0,"SCHEDULED",now.plus(Duration.ofDays(2)),Map.of("serviceAddress","Indiranagar, Bengaluru","preferredSlot","10:00 AM"),now));
            bookings.save(booking("REGISTRATION",event,u,vs,2,998.0,"CONFIRMED",now.plus(Duration.ofDays(30)),Map.of("attendeeNames","Aarav, Diya"),now));
            bookings.save(booking("SESSION",tutor,u,vs,1,700.0,"PENDING",now.plus(Duration.ofDays(5)),Map.of("studentName","Aarav","subject","Mathematics"),now));
            bookings.save(booking("RESERVATION",rental,u,vs,1,4400.0,"PICKUP",now.plus(Duration.ofDays(7)),Map.of("pickupDate",now.plus(Duration.ofDays(7)).toString(),"returnDate",now.plus(Duration.ofDays(9)).toString()),now));
            bookings.save(booking("RESERVATION",property,u,vs,1,0.0,"VISIT_SCHEDULED",now.plus(Duration.ofDays(3)),Map.of("pickupDate",now.plus(Duration.ofDays(3)).toString(),"visitTime","5:30 PM"),now));
        }
        if(applications.findByUserIdOrderByCreatedAtDesc(u.getId()).isEmpty()){
            applications.save(JobApplication.builder().postId(job.getId()).userId(u.getId()).vendorId(job.getVendorId()).resumeUrl("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf").resumePublicId("demo-resume")
                .coverLetter("I am interested in the Spring Boot developer role and have experience building REST APIs.").status("INTERVIEW")
                .interviewDate(now.plus(Duration.ofDays(4))).interviewNotes("Technical round - online meeting").statusHistory(new ArrayList<>(List.of(
                    hist("SUBMITTED",u,"Application submitted",now.minus(Duration.ofDays(3))),hist("UNDER_REVIEW",u,"Application under review",now.minus(Duration.ofDays(2))),hist("SHORTLISTED",u,"Shortlisted for next round",now.minus(Duration.ofDays(1))),hist("INTERVIEW",u,"Interview scheduled",now)))).createdAt(now.minus(Duration.ofDays(3))).updatedAt(now).build());
        }
    }

    private Booking booking(String type,Post p,User u,Map<String,Vendor> vs,int qty,double amount,String status,Instant date,Map<String,Object> details,Instant now){
        return Booking.builder().bookingType(type).listingId(p.getId()).userId(u.getId()).vendorId(p.getVendorId()).bookingDate(LocalDate.ofInstant(date,ZoneId.systemDefault()))
            .startTime(LocalTime.of(10,0)).endTime(LocalTime.of(11,0)).quantity(qty).amount(amount).status(status).notes("Demo seeded transaction")
            .contactName(u.getName()).contactNumber(u.getMobile()).address("Bengaluru, Karnataka").details(new HashMap<>(details))
            .statusHistory(new ArrayList<>(List.of(hist(status,u,"Demo seeded transaction",now)))).createdAt(now.minus(Duration.ofDays(1))).updatedAt(now).build();
    }

    private void seedChats(Map<String,Vendor> vs,Map<String,User> cs,Instant now){
        User a=cs.get("customer1"), d=cs.get("customer2");
        User pv=users.findByEmailIgnoreCase("demo.products@asmj.co.in").orElseThrow();
        Conversation direct=findDirect(a.getId(),pv.getId());
        if(direct==null){direct=conversations.save(Conversation.builder().participants(new ArrayList<>(List.of(a.getId(),pv.getId()))).conversationType("INDIVIDUAL").lastMessage("Welcome to ASMJ demo chat").createdAt(now.minus(Duration.ofHours(3))).updatedAt(now.minus(Duration.ofMinutes(5))).build());}
        if(messages.findByConversationIdOrderByCreatedAtAsc(direct.getId()).isEmpty()){
            messages.save(Message.builder().conversationId(direct.getId()).senderId(pv.getId()).message("Hello Aarav! How can we help with your product enquiry?").messageType("TEXT").read(false).createdAt(now.minus(Duration.ofHours(2))).build());
            messages.save(Message.builder().conversationId(direct.getId()).senderId(a.getId()).message("I would like to know about delivery time.").messageType("TEXT").read(true).createdAt(now.minus(Duration.ofHours(1))).build());
            messages.save(Message.builder().conversationId(direct.getId()).senderId(pv.getId()).message("Usually 2-3 business days in Bengaluru.").messageType("TEXT").read(false).createdAt(now.minus(Duration.ofMinutes(5))).build());
        }
        Conversation group=findGroup(a.getId(),"ASMJ Demo Community");
        if(group==null) group=conversations.save(Conversation.builder().name("ASMJ Demo Community").participants(new ArrayList<>(List.of(a.getId(),d.getId(),pv.getId()))).conversationType("GROUP").createdBy(a.getId()).lastMessage("Welcome to the ASMJ community group").createdAt(now.minus(Duration.ofHours(5))).updatedAt(now.minus(Duration.ofMinutes(2))).build());
        if(messages.findByConversationIdOrderByCreatedAtAsc(group.getId()).isEmpty()){
            messages.save(Message.builder().conversationId(group.getId()).senderId(a.getId()).message("Welcome everyone! Let's discuss marketplace ideas here.").messageType("TEXT").read(true).createdAt(now.minus(Duration.ofHours(4))).build());
            messages.save(Message.builder().conversationId(group.getId()).senderId(d.getId()).message("Great! I am testing the event and tutor flows too.").messageType("TEXT").read(false).createdAt(now.minus(Duration.ofHours(2))).build());
        }
    }

    private Conversation findDirect(String a,String b){for(Conversation c:conversations.findByParticipantsContainingOrderByUpdatedAtDesc(a))if("INDIVIDUAL".equals(c.getConversationType())&&c.getParticipants()!=null&&c.getParticipants().size()==2&&c.getParticipants().contains(b))return c;return null;}
    private Conversation findGroup(String member,String name){for(Conversation c:conversations.findByParticipantsContainingOrderByUpdatedAtDesc(member))if("GROUP".equals(c.getConversationType())&&name.equals(c.getName()))return c;return null;}
    private Post find(Map<String,Post> ps,String title){return ps.values().stream().filter(p->title.equals(p.getTitle())).findFirst().orElseThrow();}
    private StatusHistory hist(String status,User u,String note,Instant at){return StatusHistory.builder().status(status).actorId(u.getId()).actorEmail(u.getEmail()).note(note).changedAt(at).build();}
    private String image(String title,int n){return "https://placehold.co/900x650/png?text="+title.replace(" ","+")+"+"+n;}
    private String slug(String s){return s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+","-").replaceAll("(^-|-$)","")+"-"+UUID.randomUUID().toString().substring(0,6);}
    private record SeedPost(String slug,String vendorEmail,String type,String title,String description,Double price,String priceType,Map<String,Object> attributes){}
}
