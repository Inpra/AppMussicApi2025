package com.example.appmussicapi.repository

import android.util.Log
import com.example.appmussicapi.data.api.CloudinaryApiClient
import com.example.appmussicapi.data.model.Song

class SongRepository {
    suspend fun getSongs(): List<Song> {
        Log.d("SongRepository", "Loading songs from Cloudinary")
        
        return listOf(
            Song(
                id = "nhac_mp3/gia_nhu_anh_lang_im",
                name = "Giá Như Anh Lặng Im",
                artist = "Soobin Hoàng Sơn",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159112/GI%C3%81_NH%C6%AF_ANH_L%E1%BA%B6NG_IM_-_OnlyC_ft._Lou_Ho%C3%A0ng_ft._Quang_H%C3%B9ng_-_Acoustic_Cover_HD_zslefe.mp3",
                imageUrl = "https://i.ytimg.com/vi/oNeBvUe-WrY/mqdefault.jpg"
            ),
            Song(
                id = "nhac_mp3/thu_cuoi",
                name = "Thu Cuối",
                artist = "MrT, Yanbi, Hằng BingBoong",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159112/Mr.T_-_Thu_Cu%E1%BB%91i_ft_Yanbi_H%E1%BA%B1ng_BingBoong_Official_MV_qb8k6g.mp3",
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR1NRNieuosft0-VCAe3n-kJ04MzlutRdh4HQ&s"
            ),
            Song(
                id = "nhac_mp3/toi_cho_co_gai_do",
                name = "Tội cho cô gái đó",
                artist = "Khắc Việt",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159111/KH%E1%BA%AEC_VI%E1%BB%86T_-_T%E1%BB%99i_Cho_C%C3%B4_G%C3%A1i_%C4%90%C3%B3_OFFICAL_MV_mnlkhy.mp3",
                imageUrl = "https://i.ytimg.com/vi/6vjgXxFHfxs/maxresdefault.jpg"
            ),
            Song(
                id = "nhac_mp3/yeu_mot_nguoi_co_le",
                name = "Yêu Một Người Có Lẽ",
                artist = "Lou Hoàng, Only C",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159111/Y%C3%AAu_M%E1%BB%99t_Ng%C6%B0%E1%BB%9Di_C%C3%B3_L%E1%BA%BD_-_Lou_Ho%C3%A0ng_-_Miu_L%C3%AA_-_Miu_L%C3%AA_Official_cuckbl.mp3",
                imageUrl = "https://photo-resize-zmp3.zmdcdn.me/w600_r300x169_jpeg/thumb_video/0/f/0f78865b35e82f33cad4bed86a322d98_1461134980.jpg"
            ),
            Song(
                id = "nhac_mp3/nguoi_ay",
                name = "Người Ấy",
                artist = "Trịnh Thăng Bình",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159110/Ng%C6%B0%E1%BB%9Di_%E1%BA%A4y_-_Tr%E1%BB%8Bnh_Th%C4%83ng_B%C3%ACnh_Lyrics_rit7l3.mp3",
                imageUrl = "https://i.ytimg.com/vi/1Tj1wSfRkZg/maxresdefault.jpg"
            ),
            Song(
                id = "nhac_mp3/yeu_lai_tu_dau",
                name = "Yêu lại từ đầu - Khắc Việt",
                artist = "Khắc Việt",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159110/KH%E1%BA%AEC_VI%E1%BB%86T_-_Y%C3%AAu_L%E1%BA%A1i_T%E1%BB%AB_%C4%90%E1%BA%A7u_Official_flotnc.mp3",
                imageUrl = "https://i.ytimg.com/vi/42Uvxt7i5dw/maxresdefault.jpg"
            ),
            Song(
                id = "nhac_mp3/con_mua_ngang_qua_2",
                name = "Cơn Mưa Ngang Qua 2-Sơn Tùng",
                artist = "Sơn Tùng",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159110/C%C6%A1n_M%C6%B0a_Ngang_Qua_Part_2_-_M-TP_Video_Lyric_Kara_hrur0v.mp3",
                imageUrl = "https://i.ytimg.com/vi/M2Y4jE_VqQg/hq720.jpg?sqp=-oaymwEhCK4FEIIDSFryq4qpAxMIARUAAAAAGAElAADIQj0AgKJD&rs=AOn4CLD2BGUOQ4IUvKbDCB-hzOmKjxO1SQ"
            ),
            Song(
                id = "nhac_mp3/con_mua_ngang_qua_3",
                name = "Cơn Mưa Ngang Qua 3-Sơn Tùng",
                artist = "Sơn Tùng",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159110/C%C6%A1n_M%C6%B0a_Ngang_Qua_Part_3_-_M-TP_Video_Lyric_Kara_yjkze9.mp3",
                imageUrl = "https://i.ytimg.com/vi/r1OtnOs-utU/hq720.jpg?sqp=-oaymwEhCK4FEIIDSFryq4qpAxMIARUAAAAAGAElAADIQj0AgKJD&rs=AOn4CLDJIXm3D5Z85XEMDS8hIDQlyhVtZA"
            ),
            Song(
                id = "nhac_mp3/dem_ngay_xa_em",
                name = "Đếm ngày xa em",
                artist = "Lou Hoàng, Only C",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159110/%C4%90%E1%BA%BFm_Ng%C3%A0y_Xa_Em_-_Only_C_ft._Lou_Ho%C3%A0ng_-_Official_MV_-_Nh%E1%BA%A1c_tr%E1%BA%BB_m%E1%BB%9Bi_hay_tuy%E1%BB%83n_ch%E1%BB%8Dn_uxuyyo.mp3",
                imageUrl = "https://imgt.taimienphi.vn/cf/Images/td/2018/4/4/loi-bai-hat-dem-ngay-xa-em.jpg"
            ),
            Song(
                id = "nhac_mp3/vo_tan",
                name = "Vỡ Tan ",
                artist = "Trịnh Thăng Bình",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159109/V%E1%BB%A1_Tan_-_Tr%E1%BB%8Bnh_Th%C4%83ng_B%C3%ACnh_tgnjl8.mp3",
                imageUrl = "https://i.ytimg.com/vi/zfp6ZaIPVjU/maxresdefault.jpg"
            ),
            Song(
                id = "nhac_mp3/con_mua_ngang_qua",
                name = "Cơn Mưa Ngang Qua ",
                artist = "Sơn Tùng",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159108/C%C6%A1n_M%C6%B0a_Ngang_Qua_yl1n5a.mp3",
                imageUrl = "https://photo-resize-zmp3.zadn.vn/w600_r1x1_jpeg/cover/f/7/1/2/f7129155397801a084d19c6cff52a7b1.jpg"
            ),
            Song(
                id = "nhac_mp3/ve_ben_anh",
                name = "Về Bênh Anh ",
                artist = "Jack 3.5 củ",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159107/OFFICIAL_MV_V%E1%BB%80_B%C3%8AN_ANH_-_Jack_G5R_yewrjg.mp3",
                imageUrl = "https://photo-resize-zmp3.zadn.vn/w600_r1x1_jpeg/cover/5/d/7/6/5d76f8f71c270b8e2adb413ae524037c.jpg"
            ),
            Song(
                id = "nhac_mp3/tuy_am",
                name = "Túy Âm",
                artist = "Xesi x Masew x Nhatnguyen",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159098/T%C3%BAy_%C3%82m_-_Xesi_x_Masew_x_Nhatnguyen_yifyjl.mp3",
                imageUrl = "https://i.ytimg.com/vi/EV-91JV4Fws/maxresdefault.jpg"
            )
        ).also {
            Log.d("SongRepository", "Loaded ${it.size} songs from Cloudinary")
        }
    }

    private fun getFallbackSongs(): List<Song> {
        return listOf(
            Song(
                id = "nhac_mp3/song1",
                name = "In My Head feat. The Chainsmokers",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753156181/In_My_Head_feat._The_Chainsmokers_y3fxey.m4a"
            ),
            Song(
                id = "nhac_mp3/song2", 
                name = "Túy Âm - Xesi x Masew x Nhatnguyen",
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159098/T%C3%BAy_%C3%82m_-_Xesi_x_Masew_x_Nhatnguyen_yifyjl.mp3"
            ),
            Song(
                id = "nhac_mp3/song3",
                name = "VỀ BÊN ANH - Jack G5R", 
                url = "https://res.cloudinary.com/dyugji8gz/video/upload/v1753159107/OFFICIAL_MV_V%E1%BB%80_B%C3%8AN_ANH_-_Jack_G5R_yewrjg.mp3"
            )
        )
    }
}
