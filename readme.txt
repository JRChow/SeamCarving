/******************************************************************************
 *  Name:       Jingran Zhou
 *  NetID:     jingranz
 *  Precept:    P03
 *
 *  Partner Name:  N/A     
 *  Partner NetID:    N/A  
 *  Partner Precept:    N/A
 *
 *  Hours to complete assignment (optional): 9
 *
 ******************************************************************************/


/******************************************************************************
 *  Describe concisely your algorithm to compute the horizontal and
 *  vertical seam. 
 *****************************************************************************/

# Vertical seam:
First, construct an energy matrix. Then, we use row-major to iterate through 
the matrix (except for the first row). For each pixel we encounter, we need to 
calculate its minimum cumulative energy. To do this, we look at the 3 pixels 
right above it, and we take the one with the minimum cumulative energy value, 
and add that value to the target pixel. 
After this, we obtain the cumulative energy matrix, and we need to reconstruct 
the seam. We start from the bottom row, find the pixel with the minimal 
cumulative energy, and add its x-coordinate to the seam array. Then, we look at 
its above three neighbours, and again take the one with the minimum cumulative 
energy value, and add its x-coordinate to the seam array. Repeat this process 
until we reach the top row. 

# Horizontal seam:
Transpose the picture, find a vertical seam, then transpose the picture back.

/******************************************************************************
 *  Describe what makes an image ideal for this seamCarving algorithm and what
 *   kind of image would not work well.
 *****************************************************************************/

Ideal image: small size; contains 'useless' information;

Bad image: huge size; ultra-high resolution image;


/******************************************************************************
 *  Give a formula (using tilde notation) for the running time 
 *  (in seconds) required to reduce image size by one row and a formula
 *  for the running time required to reduce image size by one column. 
 *  Both should be functions of W and H. Removal should involve exactly
 *  one call to the appropriate find method and one call to the 
 *  appropriate remove method. The randomPicture() method in SCUtility 
 *  may be useful.
 * 
 *  Justify your answer with sufficient data using large enough 
 *  W and H values. To dampen system effects, you may wish to perform
 *  many trials for a given value of W and H and average the results.
 *  
 *  Be sure to give the leading coefficient and the value of the exponents 
 *  as a fraction with 2 decimal places .
 *****************************************************************************/

(keep W constant) W = 5000

 H           Row removal time (seconds)     Column removal time (seconds)
--------------------------------------------------------------------------
1250         1.30108                        0.8316
2500         2.43004                        1.5067
5000         4.73072                        2.9326
10000        9.80304                        5.9138


(keep H constant) H = 5000

 W           Row removal time (seconds)     Column removal time (seconds)
--------------------------------------------------------------------------
1250           1.27324                      0.81215
2500           2.44526                      1.50795
5000           5.00372                      3.08725
10000          10.18575                     6.12983


Running time to remove one row as a function of both W and H:  

~ 1.04 * 10^{-7} W^{1.03} H^{1.05}

Running time to remove one column as a function of both W and H:  

~ 1.22 * 10^{-7} W^{0.99} H^{1.01}

/******************************************************************************
 *  Known bugs / limitations.
 *****************************************************************************/

Slow on large pictures.

/******************************************************************************
 *  Describe whatever help (if any) that you received.
 *  Don't include readings, lectures, and precepts, but do
 *  include any help from people (including course staff, lab TAs,
 *  classmates, and friends) and attribute them by name.
 *****************************************************************************/

Piazza post answered by Katheleen.

/******************************************************************************
 *  Describe any serious problems you encountered.                    
 *****************************************************************************/

None.

/******************************************************************************
 *  If you worked with a partner, assert below that you followed
 *  the protocol as described on the assignment page. Give one
 *  sentence explaining what each of you contributed.
 *****************************************************************************/

N/A

/******************************************************************************
 *  List any other comments here. Feel free to provide any feedback   
 *  on how much you learned from doing the assignment, and whether    
 *  you enjoyed doing it.                                             
 *****************************************************************************/
