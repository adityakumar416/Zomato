package com.test.zomato.view.main.home

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.test.zomato.R
import com.test.zomato.databinding.FragmentHomeBinding
import com.test.zomato.utils.AppSharedPreferences
import com.test.zomato.utils.EnableDeviceLocationBottomSheetFragment
import com.test.zomato.utils.MyHelper
import com.test.zomato.utils.PrefKeys
import com.test.zomato.view.cart.RestaurantDetailsActivity
import com.test.zomato.view.cart.interfaces.RestaurantsClickListener
import com.test.zomato.view.location.SelectAddressActivity
import com.test.zomato.view.location.models.UserSavedAddress
import com.test.zomato.view.login.repository.UserViewModel
import com.test.zomato.view.main.JoinGoldActivity
import com.test.zomato.view.main.home.adapter.AllRestaurantsAdapter
import com.test.zomato.view.main.home.adapter.ExploreItemAdapter
import com.test.zomato.view.main.home.adapter.WhatsOnYourMindItemAdapter
import com.test.zomato.view.main.home.models.ExploreData
import com.test.zomato.view.main.home.models.FoodItem
import com.test.zomato.view.main.home.models.RestaurantDetails
import com.test.zomato.view.main.home.models.WhatsOnYourMindItemData
import com.test.zomato.view.profile.ProfileActivity
import com.test.zomato.viewModels.MainViewModel
import java.util.Locale

class HomeFragment : Fragment(), RestaurantsClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var exploreAdapter: ExploreItemAdapter
    private lateinit var exploreList: List<ExploreData>
    private lateinit var itemList: List<WhatsOnYourMindItemData>
    private lateinit var whatsOnYourMindItemAdapter: WhatsOnYourMindItemAdapter
    private val myHelper by lazy { MyHelper(requireActivity()) }
    private lateinit var userViewModel: UserViewModel

    private lateinit var mainViewModel: MainViewModel

    private lateinit var allRestaurantsAdapter: AllRestaurantsAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var savedAddresses: List<UserSavedAddress>
    private val REQUEST_CODE_SPEECH_INPUT = 10
    private val appSharedPreferences by lazy { activity?.let { AppSharedPreferences.getInstance(it) } }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        myHelper.setStatusBarIconColor(requireActivity(), false)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        val isSkipBtnClick = appSharedPreferences?.getBoolean(PrefKeys.SKIP_BTN_CLICK)

        // when user click on skip btn
        if (isSkipBtnClick == true) {
            binding.joinPrimeLayout.visibility = View.GONE
            binding.profile.visibility = View.GONE
            binding.vegAndNonVegMode.visibility = View.GONE
            binding.menuIcon.visibility = View.VISIBLE

            binding.menuIcon.setOnClickListener {
                val intent = Intent(context, ProfileActivity::class.java)
                activity?.startActivity(intent)
            }

        } else {
            // user user sign in
            binding.joinPrimeLayout.visibility = View.VISIBLE
            binding.profile.visibility = View.VISIBLE
            binding.vegAndNonVegMode.visibility = View.VISIBLE
            binding.menuIcon.visibility = View.GONE

            // merge local saved address with current login user after delete local address from roomDB
            mergeLocalAddressWithCurrentUserProfile()
        }

        //Toast.makeText(requireActivity(), "${myHelper.numberIs()}", Toast.LENGTH_SHORT).show()

        binding.userLocation.setOnClickListener {
            val intent = Intent(context, SelectAddressActivity::class.java)
            activity?.startActivity(intent)
            activity?.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top)

        }

        binding.profile.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            activity?.startActivity(intent)
        }

        binding.joinPrime.setOnClickListener {
            val intent = Intent(context, JoinGoldActivity::class.java)
            // val intent = Intent(context, RestaurantDetailsActivity::class.java)
            activity?.startActivity(intent)
        }


        exploreList = listOf(
            ExploreData(
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRb8TsuNu3duKx9zP4yhh80TX8sTm7ON4PrgA&s",
                "Everyday",
                "Type A"
            ),
            ExploreData(R.drawable.offer_tag, "Offers", "Type A"),
            ExploreData(
                "https://cdn3d.iconscout.com/3d/premium/thumb/fast-food-and-drink-3d-icon-download-in-png-blend-fbx-gltf-file-formats--pizza-cold-burger-pack-icons-7785996.png",
                "Plan\n a party",
                "Type A"
            ),
            ExploreData(
                "https://png.pngtree.com/png-clipart/20240117/original/pngtree-cartoon-flat-style-pancakes-honey-gourmet-delicious-food-to-satisfy-hunger-png-image_14124400.png",
                "Gourmet",
                "Type A"
            ),
            ExploreData(
                "https://static.vecteezy.com/system/resources/previews/027/144/634/non_2x/fresh-tasty-mix-fruit-salad-isolated-on-transparent-background-png.png",
                "Healthy",
                "Type A"
            ),
            ExploreData(
                "https://static.vecteezy.com/system/resources/thumbnails/026/772/933/small_2x/train-with-ai-generated-free-png.png",
                "Food\n on train",
                "Type A"
            ),
            ExploreData(
                "https://static.vecteezy.com/system/resources/previews/050/702/057/non_2x/flat-banner-with-red-gift-card-on-white-background-flyer-design-brochure-background-png.png",
                "Gift cards",
                "Type A"
            ),
        )

        // Set up Horizontal RecyclerView
        exploreAdapter = ExploreItemAdapter(exploreList)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView.adapter = exploreAdapter


        itemList = listOf(
            WhatsOnYourMindItemData(
                image1 = "https://png.pngtree.com/png-vector/20240405/ourmid/pngtree-burger-cold-drink-and-chips-png-image_12264894.png",  // Hot Dog
                image2 = "https://png.pngtree.com/png-vector/20241030/ourmid/pngtree-pizza-bread-cheese-drop-art-work-png-image_14183274.png",  // Pizza
                itemName1 = "Cold Drink",
                itemName2 = "Pizza"
            ),
            WhatsOnYourMindItemData(
                image1 = "https://png.pngtree.com/png-clipart/20231109/original/pngtree-burger-fast-food-watercolor-png-image_13521681.png",  // Burger
                image2 = "https://png.pngtree.com/png-vector/20240918/ourmid/pngtree-a-serving-of-french-fries-sits-in-a-perfectly-golden-brown-png-image_13865133.png",  // French Fries
                itemName1 = "Burger",
                itemName2 = "French Fries"
            ),
            WhatsOnYourMindItemData(
                image1 = "https://file.aiquickdraw.com/imgcompressed/img/compressed_861ccffad7f42871b64382b0145965e5.webp",  // Fried Rice
                image2 = "https://static.vecteezy.com/system/resources/previews/027/144/737/non_2x/penne-pasta-with-tomato-sauce-parmesan-cheese-and-basil-on-transparent-background-png.png",  // Pasta
                itemName1 = "Paneer",
                itemName2 = "Pasta"
            ),
            WhatsOnYourMindItemData(
                image1 = "https://static.vecteezy.com/system/resources/thumbnails/036/007/655/small/ai-generated-hot-dog-sandwich-on-transparent-background-ai-generated-free-png.png",  // Hot Dog
                image2 = "https://png.pngtree.com/png-vector/20240725/ourmid/pngtree-paratha-indian-breakfast-art-work-png-image_13230276.png",  // Donut
                itemName1 = "Hot Dog",
                itemName2 = "Paratha"
            ),
            WhatsOnYourMindItemData(
                image1 = "https://static.vecteezy.com/system/resources/previews/025/250/377/non_2x/yummy-fried-chicken-cartoon-illustrations-and-clipart-of-tasty-fast-food-meal-generative-ai-png.png",  // Chicken Wings
                image2 = "https://wallpapers.com/images/hd/assorted-soda-cansand-cupwith-straw-ybmha4awenxwj2qv.png",  // Soft Drink
                itemName1 = "Chicken\nWings",
                itemName2 = "Soft Drink"
            ),
            WhatsOnYourMindItemData(
                image1 = "https://static.vecteezy.com/system/resources/thumbnails/051/868/307/small_2x/crispy-golden-spring-rolls-with-dipping-sauce-free-png.png",  // Taco
                image2 = "https://static.vecteezy.com/system/resources/thumbnails/041/277/335/small/ai-generated-sandwich-with-ham-cheese-tomatoes-and-lettuce-isolated-on-transparent-background-png.png",  // Sandwich
                itemName1 = "Rolls",
                itemName2 = "Sandwich"
            ),
            WhatsOnYourMindItemData(
                image1 = "https://png.pngtree.com/png-vector/20241030/ourmid/pngtree-watch-movie-popcorn-maker-snack-foods-png-image_14171554.png",  // Popcorn
                image2 = "https://static.vecteezy.com/system/resources/thumbnails/036/627/182/small_2x/ai-generated-crunchy-french-fries-on-transparent-background-png.png",  // Fries
                itemName1 = "Popcorn",
                itemName2 = "Fries"
            ),
            WhatsOnYourMindItemData(
                image1 = "https://static.vecteezy.com/system/resources/previews/028/082/776/non_2x/chocolate-and-strawberry-ice-cream-cone-isolated-on-transparent-background-generative-with-ai-free-png.png",  // Ice Cream
                image2 = "https://png.pngtree.com/png-vector/20240725/ourmid/pngtree-paratha-indian-breakfast-art-work-png-image_13230276.png",  // Donut
                itemName1 = "Ice Cream",
                itemName2 = "Paratha"
            ),
            WhatsOnYourMindItemData(
                image1 = "https://static.vecteezy.com/system/resources/previews/037/799/048/non_2x/bowl-of-salad-in-black-bowl-top-view-isolated-on-transparent-background-png.png",  // Salad
                image2 = "https://static.vecteezy.com/system/resources/previews/049/669/932/non_2x/refreshing-berry-and-peach-smoothie-with-a-transparent-background-png.png",  // Smoothie
                itemName1 = "Salad",
                itemName2 = "Smoothie"
            ),
            WhatsOnYourMindItemData(
                image1 = "https://static.vecteezy.com/system/resources/previews/027/144/737/non_2x/penne-pasta-with-tomato-sauce-parmesan-cheese-and-basil-on-transparent-background-png.png",  // Pizza
                image2 = "https://static.vecteezy.com/system/resources/thumbnails/036/743/856/small_2x/ai-generated-a-plate-of-chicken-biryani-with-chicken-and-spices-on-transparent-background-free-png.png",  // Chicken
                itemName1 = "Pizza",
                itemName2 = "Chicken"
            ),
            WhatsOnYourMindItemData(
                image1 = "https://png.pngtree.com/png-vector/20240801/ourmid/pngtree-fried-rice-with-green-peas-carrot-png-image_13240298.png",  // Fried Rice
                image2 = "https://png.pngtree.com/png-vector/20241016/ourmid/pngtree-delicious-popcorn-chicken-pieces-in-a-striped-paper-bucket-perfect-for-png-image_14097003.png",  // Popcorn Chicken
                itemName1 = "Fried Rice",
                itemName2 = "Popcorn\nChicken"
            ),
            WhatsOnYourMindItemData(
                image1 = "https://static.vecteezy.com/system/resources/thumbnails/027/182/570/small_2x/assorted-pile-of-colorful-delicious-donut-isolated-on-a-transparent-background-png.png",  // Donut
                image2 = "https://png.pngtree.com/png-vector/20240918/ourmid/pngtree-a-serving-of-french-fries-sits-in-a-perfectly-golden-brown-png-image_13865133.png",  // French Fries
                itemName1 = "Donut",
                itemName2 = "French Fries"
            ),
            WhatsOnYourMindItemData(
                image1 = "https://static.vecteezy.com/system/resources/previews/021/952/448/non_2x/southern-fried-chicken-fried-chicken-transparent-background-png.png",  // Fried Chicken
                image2 = "https://static.vecteezy.com/system/resources/previews/029/107/695/non_2x/chocolate-milkshake-with-toppings-on-a-transparent-background-ai-generative-free-png.png",  // Shake
                itemName1 = "Fried Chicken",
                itemName2 = "Shake"
            ),
            WhatsOnYourMindItemData(
                image1 = "https://png.pngtree.com/png-vector/20240801/ourmid/pngtree-fried-rice-with-green-peas-carrot-png-image_13240298.png",  // Fried Rice
                image2 = "https://png.pngtree.com/png-clipart/20230518/original/pngtree-kadai-paneer-curry-png-image_9164812.png",  // Sushi
                itemName1 = "Fried Rice",
                itemName2 = "Paneer"
            )
        )


        whatsOnYourMindItemAdapter = WhatsOnYourMindItemAdapter(itemList)
        binding.recyclerView2.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerView2.adapter = whatsOnYourMindItemAdapter


        val restaurants = listOf(
            RestaurantDetails(
                id = 1,
                imageSliderImages = listOf(
                    "https://media.istockphoto.com/id/1442417585/photo/person-getting-a-piece-of-cheesy-pepperoni-pizza.jpg?s=612x612&w=0&k=20&c=k60TjxKIOIxJpd4F4yLMVjsniB4W1BpEV4Mi_nb4uJU=",
                    "https://img.freepik.com/premium-photo/pizza-slice-presentation-dark-background_495832-1720.jpg?semt=ais_hybrid",
                    "https://img.freepik.com/free-photo/delicious-pizza-indoors_23-2150873874.jpg",
                    "https://img.freepik.com/free-photo/delicious-pizza-indoors_23-2150873870.jpg"
                ),
                bookmarkIcon = R.drawable.bookmark_icon,
                closeEyeIcon = R.drawable.eye_closed_icon,
                time = "12 mins",
                distance = "5 km",
                restaurantName = "The Postman Kitchen",
                rating = 4.5f,
                foodType1 = "North Indian",
                foodType2 = "Chinese",
                costForOne = "300 for one",
                discountText = "30% OFF up to ₹75",
                timeToReach = "12 mins",
                restaurantLocation = "Sector 12, New Delhi",
                offerUpToText = "Save up to ₹75",
                totalOffers = 2,
                recommendedFoodList = listOf(
                    FoodItem(
                        id = 101,
                        foodType = "Non-Veg",
                        foodName = "Butter Chicken",
                        foodRating = 4.6,
                        totalRatingCount = 120,
                        foodPrice = "250",
                        foodDescription = "Delicious creamy butter chicken served with naan.",
                        foodImage = "https://tandooribitesie.com/wp-content/uploads/2024/09/Butter-Chicken.webp",
                        foodQuantity = 0,
                        restaurantId = 1,
                        restaurantName = "The Postman Kitchen",
                        foodOffer = "37",
                        foodSize = "Full",
                        eggFood = false,
                        sweetFood = false
                    ),
                    FoodItem(
                        id = 102,
                        foodType = "Veg",
                        foodName = "Veg Pakora",
                        foodRating = 4.3,
                        totalRatingCount = 80,
                        foodPrice = "150",
                        foodDescription = "Crispy fried vegetable fritters.",
                        foodImage = "https://royalepunjabuk.com/wp-content/uploads/2024/06/Paneer-Pakora-4-Piece.png",
                        foodQuantity = 0,
                        restaurantId = 1,
                        restaurantName = "The Postman Kitchen",
                        foodOffer = "25",
                        foodSize = "Small",
                        eggFood = false,
                        sweetFood = false
                    )
                )
            ),
            RestaurantDetails(
                id = 2,
                imageSliderImages = listOf(
                    "https://img.freepik.com/free-photo/fast-food-burgers-with-french-fries-cutlery_23-2148290593.jpg",
                    "https://img.freepik.com/premium-photo/pizza-slice-presentation-dark-background_495832-1720.jpg?semt=ais_hybrid",
                    "https://img.freepik.com/free-photo/delicious-pizza-indoors_23-2150873874.jpg",
                    "https://img.freepik.com/free-photo/delicious-pizza-indoors_23-2150873870.jpg"
                ),
                bookmarkIcon = R.drawable.bookmark_icon,
                closeEyeIcon = R.drawable.eye_closed_icon,
                time = "15 mins",
                distance = "6 km",
                restaurantName = "Bistro 24",
                rating = 4.2f,
                foodType1 = "Italian",
                foodType2 = "Mediterranean",
                costForOne = "350 for one",
                discountText = "20% OFF up to ₹50",
                timeToReach = "15 mins",
                restaurantLocation = "Connaught Place, New Delhi",
                offerUpToText = "Save up to ₹50",
                totalOffers = 1,
                recommendedFoodList = listOf(
                    FoodItem(
                        id = 103,
                        foodType = "Veg",
                        foodName = "Margherita Pizza",
                        foodRating = 4.5,
                        totalRatingCount = 150,
                        foodPrice = "300",
                        foodDescription = "Classic Margherita pizza with mozzarella and basil.",
                        foodImage = "https://png.pngtree.com/png-clipart/20230413/original/pngtree-italian-pizza-gourmet-fast-food-creative-cartoon-decoration-pattern-png-image_9049948.png",
                        foodQuantity = 0,
                        restaurantId = 2,
                        restaurantName = "Bistro 24",
                        foodOffer = "40",
                        foodSize = "Medium",
                        eggFood = false,
                        sweetFood = false
                    ),
                    FoodItem(
                        id = 104,
                        foodType = "Veg",
                        foodName = "Penne Arrabbiata",
                        foodRating = 4.4,
                        totalRatingCount = 95,
                        foodPrice = "280",
                        foodDescription = "Spicy pasta with tomato sauce and chili flakes.",
                        foodImage = "https://static.vecteezy.com/system/resources/previews/042/600/517/non_2x/ai-generated-tasty-italian-penne-all-arrabbiata-dissh-isolated-on-transparent-background-free-png.png",
                        foodQuantity = 0,
                        restaurantId = 2,
                        restaurantName = "Bistro 24",
                        foodOffer = "30",
                        foodSize = "Large",
                        eggFood = false,
                        sweetFood = false
                    )
                )
            ),
            RestaurantDetails(
                id = 3,
                imageSliderImages = listOf(
                    "https://img.freepik.com/premium-photo/pizza-slice-presentation-dark-background_495832-1720.jpg?semt=ais_hybrid",
                    "https://img.freepik.com/free-photo/delicious-pizza-indoors_23-2150873874.jpg",
                    "https://media.istockphoto.com/id/1442417585/photo/person-getting-a-piece-of-cheesy-pepperoni-pizza.jpg?s=612x612&w=0&k=20&c=k60TjxKIOIxJpd4F4yLMVjsniB4W1BpEV4Mi_nb4uJU=",
                    "https://img.freepik.com/free-photo/delicious-pizza-indoors_23-2150873870.jpg"
                ),
                bookmarkIcon = R.drawable.bookmark_icon,
                closeEyeIcon = R.drawable.eye_closed_icon,
                time = "18 mins",
                distance = "8 km",
                restaurantName = "Spice Haven",
                rating = 4.7f,
                foodType1 = "Indian",
                foodType2 = "Thai",
                costForOne = "450 for one",
                discountText = "25% OFF up to ₹100",
                timeToReach = "18 mins",
                restaurantLocation = "Khan Market, New Delhi",
                offerUpToText = "Save up to ₹100",
                totalOffers = 3,
                recommendedFoodList = listOf(
                    FoodItem(
                        id = 105,
                        foodType = "Non-Veg",
                        foodName = "Pad Thai",
                        foodRating = 4.8,
                        totalRatingCount = 200,
                        foodPrice = "350",
                        foodDescription = "Authentic Thai stir-fried noodles with peanuts and lime.",
                        foodImage = "https://png.pngtree.com/png-vector/20241110/ourmid/pngtree-shrimp-pad-thai-dish-png-image_14346630.png",
                        foodQuantity = 0,
                        restaurantId = 3,
                        restaurantName = "Spice Haven",
                        foodOffer = "50",
                        foodSize = "Full",
                        eggFood = false,
                        sweetFood = false
                    ),
                    FoodItem(
                        id = 106,
                        foodType = "Veg",
                        foodName = "Spring Rolls",
                        foodRating = 4.6,
                        totalRatingCount = 90,
                        foodPrice = "180",
                        foodDescription = "Crispy rolls filled with vegetables and served with dipping sauce.",
                        foodImage = "https://static.vecteezy.com/system/resources/thumbnails/025/222/148/small_2x/chinese-traditional-spring-rolls-isolated-on-transparent-background-png.png",
                        foodQuantity = 0,
                        restaurantId = 3,
                        restaurantName = "Spice Haven",
                        foodOffer = "20",
                        foodSize = "Small",
                        eggFood = false,
                        sweetFood = false
                    )
                )
            ),
            RestaurantDetails(
                id = 4,
                imageSliderImages = listOf(
                    "https://img.freepik.com/premium-photo/succulent-chicken-tikka-sliders-top-fast-food-chicken-tikka-image-photography_1020697-116190.jpg",
                    "https://img.freepik.com/free-photo/delicious-pizza-indoors_23-2150873874.jpg",
                    "https://img.freepik.com/free-photo/delicious-pizza-indoors_23-2150873870.jpg",
                    "https://media.istockphoto.com/id/1442417585/photo/person-getting-a-piece-of-cheesy-pepperoni-pizza.jpg?s=612x612&w=0&k=20&c=k60TjxKIOIxJpd4F4yLMVjsniB4W1BpEV4Mi_nb4uJU="
                ),
                bookmarkIcon = R.drawable.bookmark_icon,
                closeEyeIcon = R.drawable.eye_closed_icon,
                time = "10 mins",
                distance = "4 km",
                restaurantName = "Urban Grill",
                rating = 4.3f,
                foodType1 = "Continental",
                foodType2 = "Mexican",
                costForOne = "280 for one",
                discountText = "15% OFF up to ₹50",
                timeToReach = "10 mins",
                restaurantLocation = "Saket, New Delhi",
                offerUpToText = "Save up to ₹50",
                totalOffers = 1,
                recommendedFoodList = listOf(
                    FoodItem(
                        id = 107,
                        foodType = "Non-Veg",
                        foodName = "Grilled Chicken",
                        foodRating = 4.5,
                        totalRatingCount = 130,
                        foodPrice = "350",
                        foodDescription = "Juicy grilled chicken served with vegetables and sauce.",
                        foodImage = "https://images.rawpixel.com/image_800/cHJpdmF0ZS9zci9pbWFnZXMvd2Vic2l0ZS8yMDIzLTExL3Jhd3BpeGVsX29mZmljZV8zNF9zYXZvcnlfb3Zlbi1yb2FzdGVkX3dob2xlX2NoaWNrZW5fdG9wcGVkX3dpdF8yM2Y0ODU2Mi0wMzUzLTQzNzQtOWFlNy0wMmY2OWRiYTEzZWRfMi5qcGc.jpg",
                        foodQuantity = 0,
                        restaurantId = 4,
                        restaurantName = "Urban Grill",
                        foodOffer = "25",
                        foodSize = "Large",
                        eggFood = false,
                        sweetFood = false
                    ),
                    FoodItem(
                        id = 108,
                        foodType = "Veg",
                        foodName = "Veg Burrito",
                        foodRating = 4.2,
                        totalRatingCount = 80,
                        foodPrice = "250",
                        foodDescription = "A healthy Mexican burrito filled with rice, beans, and veggies.",
                        foodImage = "https://assets.epicurious.com/photos/57978b27c9298e54495917d5/master/pass/black-bean-and-vegetable-burritos.jpg",
                        foodQuantity = 0,
                        restaurantId = 4,
                        restaurantName = "Urban Grill",
                        foodOffer = "15",
                        foodSize = "Small",
                        eggFood = false,
                        sweetFood = false
                    )
                )
            )
        )



        allRestaurantsAdapter = AllRestaurantsAdapter(restaurants, this)
        binding.recyclerView3.layoutManager = LinearLayoutManager(context)
        binding.recyclerView3.adapter = allRestaurantsAdapter


        binding.micIcon.setOnClickListener {
                // Create an intent for speech recognition
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
                }

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(activity, "Speech recognition is not supported on this device.", Toast.LENGTH_SHORT).show()
                }
            }



        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                val searchText = p0.toString().trim()

                val list = if (searchText.isNotEmpty()) {
                    restaurants.filter { it.restaurantName.contains(searchText,
                            ignoreCase = true
                        ) || it.recommendedFoodList[0].foodName.contains(
                            searchText,
                            ignoreCase = true
                        )
                    }
                } else {
                    restaurants
                }

                allRestaurantsAdapter.updateList(list)
                allRestaurantsAdapter.notifyDataSetChanged()
            }
        })


        mainViewModel.getAllAddresses(myHelper.numberIs())
        // Observe the LiveData for saved addresses
        mainViewModel.addresses.observe(viewLifecycleOwner) { addresses ->
            if (addresses.isNotEmpty()) {
                savedAddresses = addresses
                updateToolbarLocation()
            }else{
                setLocationOnToolbar()
            }
        }


        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                // Extract the result from the data
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (!result.isNullOrEmpty()) {
                    // Set the recognized speech text to the search EditText
                    binding.search.setText(result[0])
                } else {
                    Toast.makeText(activity, "No speech input recognized", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    override fun onStart() {
        super.onStart()
        fetchUserData(myHelper.numberIs())

        // merge local saved address with current login user after delete local address from roomDB
       // mergeLocalAddressWithCurrentUserProfile()

        Handler().postDelayed({
            mainViewModel.getAllAddresses(myHelper.numberIs())
        },300)
    }

    // merge local saved address with current login user after delete local address from roomDB
    private fun mergeLocalAddressWithCurrentUserProfile() {
        mainViewModel.getAllAddresses("")
        activity?.let {
            mainViewModel.addresses.observe(it) { addresses ->
                if (addresses.isNotEmpty()) {
                    val localSavedAddress =
                        addresses.map { it.copy(currentUserNumber = myHelper.numberIs()) }
                    mainViewModel.saveAddressesForUser(localSavedAddress)
                }
            }
        }
    }


    private fun fetchUserData(userPhoneNumber: String) {
        if (userPhoneNumber.isNotEmpty()) {
            userViewModel.getUserByPhoneNumber(userPhoneNumber)

            activity?.let {
                userViewModel.userLiveData.observe(it) { user ->
                    user?.let {
                        Log.d("userDataProfile", "fetchUserData: $user")

                        if (!user.imageUrl.isNullOrEmpty()) {
                            Glide.with(this).load(user.imageUrl).into(binding.userImage)
                            binding.userFirstCharacter.visibility = View.GONE
                            binding.userImage.visibility = View.VISIBLE
                        } else {
                            binding.userFirstCharacter.visibility = View.VISIBLE
                            binding.userImage.visibility = View.GONE
                        }
                    }
                }
            }
        }

    }

    private fun updateToolbarLocation() {
        val selectedAddress = getSelectedAddress()
        if (selectedAddress != null) {
            val address = selectedAddress.selectedLocation.split(",")
            binding.userBlockLocation.text = address[0]
            binding.address.text = "${address[1]}, ${address[2]}"
        } else {
            setLocationOnToolbar()
        }
    }

    private fun getSelectedAddress(): UserSavedAddress? {
        return savedAddresses.find { it.addressSelected }
    }

    private fun setLocationOnToolbar() {
        if (myHelper.checkLocationPermission() && myHelper.isLocationEnable()) {
            try {
                activity?.let {
                    fusedLocationClient.lastLocation.addOnCompleteListener(it) { task ->
                        val location: Location? = task.result
                        if (location != null) {
                            val locationData = myHelper.extractAddressDetails(
                                location.latitude,
                                location.longitude
                            )
                            binding.userBlockLocation.text = locationData?.block
                            binding.address.text = "${locationData?.locality},${locationData?.state}"
                        } else {
                            setLocationOnToolbarFromSharedprefrence()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "GPS is not working properly", Toast.LENGTH_SHORT).show()
            }
        } else {
            setLocationOnToolbarFromSharedprefrence()
        }
    }

    private fun setLocationOnToolbarFromSharedprefrence() {
        val locationData =
            myHelper.extractAddressDetails(myHelper.getLatitude(), myHelper.getLongitude())
        binding.userBlockLocation.text = locationData?.block
        binding.address.text = "${locationData?.locality},${locationData?.state}"
    }



    override fun onRestaurantsClick(restaurantDetails: RestaurantDetails) {
        val intent = Intent(context, RestaurantDetailsActivity::class.java)
        intent.putExtra("restaurantDetails", restaurantDetails)
        activity?.startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        if (!myHelper.checkLocationPermission() || !myHelper.isLocationEnable()) {
            val enableDeviceLocationBottomSheetFragment = EnableDeviceLocationBottomSheetFragment()
            activity?.supportFragmentManager?.let {
                enableDeviceLocationBottomSheetFragment.show(
                    it,
                    "enableDeviceLocationBottomSheetFragment"
                )
            }
        }
    }


}
