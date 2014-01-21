package com.example.basexxmarktests;

public class Queries {

    /*
     * -- Q1.Return the name of the person with ID `person0'.
     */
    public static String getQ1(String db_name) {
        String ret = getAuction(db_name);
        ret += "for $b in $auction/site/people/person[@id = \"person0\"] return $b/name/text()";
        return ret;
    }

    /*
     * -- Q2. Return the initial increases of all open auctions.
     */
    public static String getQ2(String db_name) {
        String ret = getAuction(db_name);
        ret += "for $b in $auction/site/open_auctions/open_auction\n";
        ret += "return <increase>{$b/bidder[1]/increase/text()}</increase>";
        return ret;
    }

    /*
     * -- Q3. Return the IDs of all open auctions whose current -- increase is at least
     * twice as high as the initial increase.
     */
    public static String getQ3(String db_name) {
        String ret = getAuction(db_name);
        ret += "for $b in $auction/site/open_auctions/open_auction\n";
        ret += "where zero-or-one($b/bidder[1]/increase/text()) * 2 <= $b/bidder[last()]/increase/text()\n";
        ret += "return\n";
        ret += "  <increase\n";
        ret += "  first=\"{$b/bidder[1]/increase/text()}\"\n";
        ret += "  last=\"{$b/bidder[last()]/increase/text()}\"/>";
        return ret;
    }

    /*
     * -- Q4. List the reserves of those open auctions where a -- certain person issued a
     * bid before another person.
     */
    public static String getQ4(String db_name) {
        String ret = getAuction(db_name);
        ret += "for $b in $auction/site/open_auctions/open_auction\n";
        ret += "where\n";
        ret += "  some $pr1 in $b/bidder/personref[@person = \"person20\"],\n";
        ret += "       $pr2 in $b/bidder/personref[@person = \"person51\"]\n";
        ret += "  satisfies $pr1 << $pr2\n";
        ret += "return <history>{$b/reserve/text()}</history>";
        return ret;
    }

    /*
     * -- Q5. How many sold items cost more than 40?
     */
    public static String getQ5(String db_name) {
        String ret = getAuction(db_name);
        ret += "count(\n";
        ret += "  for $i in $auction/site/closed_auctions/closed_auction\n";
        ret += "  where $i/price/text() >= 40\n";
        ret += "  return $i/price\n";
        ret += ")";
        return ret;
    }

    /*
     * -- Q6. How many items are listed on all continents?
     */
    public static String getQ6(String db_name) {
        String ret = getAuction(db_name);
        ret += "for $b in $auction//site/regions return count($b//item)";
        return ret;
    }

    /*
     * -- Q7. How many pieces of prose are in our database?}
     * */
    public static String getQ7(String db_name) {
        String ret = getAuction(db_name);
        ret += "for $p in $auction/site\n";
        ret += "return\n";
        ret += "  count($p//description) + count($p//annotation) + count($p//emailaddress)";
        return ret;
    }

    /*
     * -- Q8. List the names of persons and the number of items they bought.
     * --     (joins person, closed\_auction)}
     * */
    public static String getQ8(String db_name) {
        String ret = getAuction(db_name);
        ret += "for $p in $auction/site/people/person\n";
        ret += "let $a :=\n";
        ret += "  for $t in $auction/site/closed_auctions/closed_auction\n";
        ret += "  where $t/buyer/@person = $p/@id\n";
        ret += "  return $t\n";
        ret += "return <item person=\"{$p/name/text()}\">{count($a)}</item>";
        return ret;
    }

    /*
     * -- Q9. List the names of persons and the names of the items they bought
     * --     in Europe.  (joins person, closed\_auction, item)}
     * */
    public static String getQ9(String db_name) {
        String ret = getAuction(db_name);
        ret += "let $ca := $auction/site/closed_auctions/closed_auction return\n";
        ret += "let\n";
        ret += "    $ei := $auction/site/regions/europe/item\n";
        ret += "for $p in $auction/site/people/person\n";
        ret += "let $a :=\n";
        ret += "  for $t in $ca\n";
        ret += "  where $p/@id = $t/buyer/@person\n";
        ret += "  return\n";
        ret += "    let $n := for $t2 in $ei where $t/itemref/@item = $t2/@id return $t2\n";
        ret += "    return <item>{$n/name/text()}</item>\n";
        ret += "return <person name=\"{$p/name/text()}\">{$a}</person>";
        return ret;
    }


    /*
     * -- Q10. List all persons according to their interest;
     * --      use French markup in the result.
     * */
    public static String getQ10(String db_name) {
        String ret = getAuction(db_name);
        ret += "for $i in\n";
        ret += "  distinct-values($auction/site/people/person/profile/interest/@category)\n";
        ret += "let $p :=\n";
        ret += "  for $t in $auction/site/people/person\n";
        ret += "  where $t/profile/interest/@category = $i\n";
        ret += "  return\n";
        ret += "    <personne>\n";
        ret += "      <statistiques>\n";
        ret += "        <sexe>{$t/profile/gender/text()}</sexe>\n";
        ret += "        <age>{$t/profile/age/text()}</age>\n";
        ret += "        <education>{$t/profile/education/text()}</education>\n";
        ret += "        <revenu>{fn:data($t/profile/@income)}</revenu>\n";
        ret += "      </statistiques>\n";
        ret += "      <coordonnees>\n";
        ret += "        <nom>{$t/name/text()}</nom>\n";
        ret += "        <rue>{$t/address/street/text()}</rue>\n";
        ret += "        <ville>{$t/address/city/text()}</ville>\n";
        ret += "        <pays>{$t/address/country/text()}</pays>\n";
        ret += "        <reseau>\n";
        ret += "          <courrier>{$t/emailaddress/text()}</courrier>\n";
        ret += "          <pagePerso>{$t/homepage/text()}</pagePerso>\n";
        ret += "        </reseau>\n";
        ret += "      </coordonnees>\n";
        ret += "      <cartePaiement>{$t/creditcard/text()}</cartePaiement>\n";
        ret += "    </personne>\n";
        ret += "return <categorie>{<id>{$i}</id>, $p}</categorie>";
        return ret;
    }

    /*
     * -- Q11. For each person, list the number of items currently on sale whose
     * --      price does not exceed 0.02% of the person's income.
     * */
    public static String getQ11(String db_name) {
        String ret = getAuction(db_name);
        ret += "for $p in $auction/site/people/person\n";
        ret += "let $l :=\n";
        ret += "  for $i in $auction/site/open_auctions/open_auction/initial\n";
        ret += "  where $p/profile/@income > 5000 * exactly-one($i/text())\n";
        ret += "  return $i\n";
        ret += "return <items name=\"{$p/name/text()}\">{count($l)}</items>";
        return ret;
    }

    /*
     * -- Q12.  For each richer-than-average person, list the number of items
     * --       currently on sale whose price does not exceed 0.02% of the
     * --       person's income.
     * */
    public static String getQ12(String db_name) {
        String ret = getAuction(db_name);
        ret += "for $p in $auction/site/people/person\n";
        ret += "let $l :=\n";
        ret += "  for $i in $auction/site/open_auctions/open_auction/initial\n";
        ret += "  where $p/profile/@income > 5000 * exactly-one($i/text())\n";
        ret += "  return $i\n";
        ret += "where $p/profile/@income > 50000\n";
        ret += "return <items person=\"{$p/profile/@income}\">{count($l)}</items>";
        return ret;
    }

    /*
     * -- Q13. List the names of items registered in Australia along with
     * --      their descriptions.
     * */
    public static String getQ13(String db_name) {
        String ret = getAuction(db_name);
        ret += "for $i in $auction/site/regions/australia/item\n";
        ret += "return <item name=\"{$i/name/text()}\">{$i/description}</item>";
        return ret;
    }

    /*
     * -- Q14. Return the names of all items whose description contains the
     * --      word `gold'.
     * */
    public static String getQ14(String db_name) {
        String ret = getAuction(db_name);
        ret += "for $i in $auction/site//item\n";
        ret += "where contains(string(exactly-one($i/description)), \"gold\")\n";
        ret += "return $i/name/text()";
        return ret;
    }

    /*
     * -- Q15. Print the keywords in emphasis in annotations of closed auctions.
     * */
    public static String getQ15(String db_name) {
        String ret = getAuction(db_name);
        ret += "for $a in\n";
        ret += "  $auction/site/closed_auctions/closed_auction/annotation/description/parlist/\n";
        ret += "   listitem/\n";
        ret += "   parlist/\n";
        ret += "   listitem/\n";
        ret += "   text/\n";
        ret += "   emph/\n";
        ret += "   keyword/\n";
        ret += "   text()\n";
        ret += "return <text>{$a}</text>";
        return ret;
    }

    /*
     * -- Q16. Return the IDs of those auctions
     * --      that have one or more keywords in emphasis. (cf. Q15)
     * */
    public static String getQ16(String db_name) {
        String ret = getAuction(db_name);
        ret += "for $a in $auction/site/closed_auctions/closed_auction\n";
        ret += "where\n";
        ret += "  not(\n";
        ret += "    empty(\n";
        ret += "      $a/annotation/description/parlist/listitem/parlist/listitem/text/emph/\n";
        ret += "       keyword/\n";
        ret += "       text()\n";
        ret += "    )\n";
        ret += "  )\n";
        ret += "return <person id=\"{$a/seller/@person}\"/>";
        return ret;
    }

    /*
     * -- Q17. Which persons don't have a homepage?
     * */
    public static String getQ17(String db_name) {
        String ret = getAuction(db_name);
        ret += "for $p in $auction/site/people/person\n";
        ret += "where empty($p/homepage/text())\n";
        ret += "return <person name=\"{$p/name/text()}\"/>";
        return ret;
    }

    /*
     * -- Q18.Convert the currency of the reserve of all open auctions to
     * --     another currency.
     * */
    public static String getQ18(String db_name) {
        String ret = "declare namespace local = \"http://www.foobar.org\";\n";
        ret += "declare function local:convert($v as xs:decimal?) as xs:decimal?\n";
        ret += "{\n";
        ret += "  2.20371 * $v (: convert Dfl to Euro :)\n";
        ret += "};\n\n";
        ret += getAuction(db_name);
        ret += "for $i in $auction/site/open_auctions/open_auction\n";
        ret += "return local:convert(zero-or-one($i/reserve))";
        return ret;
    }

    /*
     * -- Q19. Give an alphabetically ordered list of all
     * --      items along with their location.
     * */
    public static String getQ19(String db_name) {
        String ret = getAuction(db_name);
        ret += "for $b in $auction/site/regions//item\n";
        ret += "let $k := $b/name/text()\n";
        ret += "order by zero-or-one($b/location) ascending empty greatest\n";
        ret += "return <item name=\"{$k}\">{$b/location/text()}</item>";
        return ret;
    }

    /*
     * -- Q20. Group customers by their
     * --      income and output the cardinality of each group.
     * */
    public static String getQ20(String db_name) {
        String ret = getAuction(db_name);
        ret += "<result>\n";
        ret += "  <preferred>\n";
        ret += "    {count($auction/site/people/person/profile[@income >= 100000])}\n";
        ret += "  </preferred>\n";
        ret += "  <standard>\n";
        ret += "    {\n";
        ret += "      count(\n";
        ret += "        $auction/site/people/person/\n";
        ret += "         profile[@income < 100000 and @income >= 30000]\n";
        ret += "      )\n";
        ret += "    }\n";
        ret += "  </standard>\n";
        ret += "  <challenge>\n";
        ret += "    {count($auction/site/people/person/profile[@income < 30000])}\n";
        ret += "  </challenge>\n";
        ret += "  <na>\n";
        ret += "    {\n";
        ret += "      count(\n";
        ret += "        for $p in $auction/site/people/person\n";
        ret += "        where empty($p/profile/@income)\n";
        ret += "        return $p\n";
        ret += "      )\n";
        ret += "    }\n";
        ret += "  </na>\n";
        ret += "</result>";
        return ret;
    }

    private static String getAuction(String db_name) {
        return "let $auction := doc('" + db_name + "') return\n";
    }

}
